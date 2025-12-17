package com.training.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.thunisoft.cloud.storage.client.base.protocol.attribtues.AttributeImpl;
import com.thunisoft.cloud.storage.client.core.store.IStoreService;
import com.training.common.PageResult;
import com.training.dto.UserCreateDTO;
import com.training.dto.UserUpdateDTO;
import com.training.entity.User;
import com.training.entity.UserExcel;
import com.training.mapper.UserMapper;
import com.training.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    private IStoreService storageService;

    /**
     * 创建用户（支持头像上传）
     */
    @Transactional
    public User createUserWithAvatar(UserCreateDTO dto, MultipartFile avatarFile) {
        // 如果上传了头像文件，先上传头像
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = uploadFile(avatarFile);
        }

        // 设置头像URL到DTO
        dto.setAvatar(avatarUrl);

        // 创建用户
        return createUser(dto);
    }

    /**
     * 创建用户
     */
    @Transactional
    public User createUser(UserCreateDTO dto) {
        // 校验用户名是否已存在
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 校验工号是否已存在
        if (userMapper.countByEmployeeNo(dto.getEmployeeNo(), null) > 0) {
            throw new RuntimeException("工号已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmployeeNo(dto.getEmployeeNo());
        user.setDept(dto.getDept());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? dto.getRole() : "employee");
        user.setStatus(1);
        user.setAvatar(dto.getAvatar());
        // 使用MD5加密密码
        String encodedPassword = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    /**
     * 更新用户（支持头像上传）
     */
    @Transactional
    public boolean updateUserWithAvatar(Long id, UserUpdateDTO dto, MultipartFile avatarFile) {
        // 如果上传了头像文件，先上传头像
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = uploadFile(avatarFile);
            dto.setAvatar(avatarUrl);
        }

        // 更新用户
        return updateUser(id, dto);
    }

    /**
     * 更新用户
     */
    @Transactional
    public boolean updateUser(Long id, UserUpdateDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }

        if (StrUtil.isNotBlank(dto.getName()))
            user.setName(dto.getName());
        if (StrUtil.isNotBlank(dto.getEmployeeNo())) {
            if (userMapper.countByEmployeeNo(dto.getEmployeeNo(), id) > 0) {
                throw new RuntimeException("工号已被使用");
            }
            user.setEmployeeNo(dto.getEmployeeNo());
        }
        if (StrUtil.isNotBlank(dto.getDept()))
            user.setDept(dto.getDept());
        if (StrUtil.isNotBlank(dto.getPhone()))
            user.setPhone(dto.getPhone());
        if (dto.getStatus() != null)
            user.setStatus(dto.getStatus());
        if (StrUtil.isNotBlank(dto.getAvatar()))
            user.setAvatar(dto.getAvatar());

        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 分页查询用户
     */
    public PageResult<User> getUserPage(Integer page, Integer size, String keyword) {
        List<User> all = userMapper.selectByCondition(keyword);
        long total = all.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, all.size());
        List<User> records = start < all.size() ? all.subList(start, end) : Collections.emptyList();
        records.forEach(user -> {
            user.setPassword( null);
            if(user.getAvatar() != null)
                user.setAvatar(storageService.preGetObjectUrl(user.getAvatar()));
        });
        return new PageResult<>(records, total, page, size);
    }

    /**
     * 根据ID获取用户
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * Excel导入用户
     */
    @Transactional
    public int importUsers(MultipartFile file) throws IOException {
        List<UserExcel> excelList = EasyExcel.read(file.getInputStream(), UserExcel.class, null)
                .sheet()
                .doReadSync();

        int successCount = 0;
        for (UserExcel excel : excelList) {
            if (StrUtil.isBlank(excel.getName()) || StrUtil.isBlank(excel.getEmployeeNo())) {
                continue;
            }

            if (userMapper.countByEmployeeNo(excel.getEmployeeNo(), null) > 0) {
                continue;
            }

            // 为 Excel 导入生成唯一的用户名，优先使用工号
    //            String username = generateUniqueUsername(excel.getEmployeeNo());

            User user = new User();
            user.setUsername(excel.getUsername());
            user.setName(excel.getName());
            user.setEmployeeNo(excel.getEmployeeNo());
            user.setDept(excel.getDept());
            user.setPhone(excel.getPhone());
            user.setRole("employee");
            user.setStatus(1);
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userMapper.insert(user);
            successCount++;
        }

        return successCount;
    }

    /**
     * Excel导出用户
     * @param keyword 关键词筛选（可选）
     */
    public void exportUsers(HttpServletResponse response, String keyword) throws IOException {
        // 根据关键词筛选用户，如果没有关键词则导出所有用户
        List<User> users = StrUtil.isNotBlank(keyword)
                ? userMapper.selectByCondition(keyword)
                : userMapper.selectAll();

        List<UserExcel> excelList = users.stream().map(user -> {
            UserExcel excel = new UserExcel();
            excel.setUsername(user.getUsername());
            excel.setName(user.getName());
            excel.setEmployeeNo(user.getEmployeeNo());
            excel.setDept(user.getDept());
            excel.setPhone(user.getPhone());
            excel.setRole(user.getRole());

            // 转换状态为中文
            if (user.getStatus() != null) {
                excel.setStatus(user.getStatus() == 1 ? "启用" : "禁用");
            } else {
                excel.setStatus("");
            }

            // 格式化创建时间（只显示日期，不显示时间）
            if (user.getCreatedAt() != null) {
                excel.setCreatedAt(user.getCreatedAt().toLocalDate().toString());
            } else {
                excel.setCreatedAt("");
            }

            return excel;
        }).collect(Collectors.toList());

        // 设置响应头，确保正确下载Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");

        // 文件名包含时间戳
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "用户列表_" + timestamp + ".xlsx";
        // 使用URLEncoder编码文件名，支持中文
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        // 设置Content-Disposition头，支持中文文件名
        response.setHeader("Content-Disposition", "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName);

        // 禁用缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 使用EasyExcel写入Excel文件，并设置列宽
        EasyExcel.write(response.getOutputStream(), UserExcel.class)
                .registerWriteHandler(new ExcelStyleUtil())  // 设置列宽
                .sheet("用户列表")
                .doWrite(excelList);
    }

    /**
     * 删除用户
     */
    @Transactional
    public boolean deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 生成唯一的用户名
     * 优先使用工号，如果工号已作为用户名使用，则添加时间戳后缀
     */
    private String generateUniqueUsername(String employeeNo) {
        if (StrUtil.isBlank(employeeNo)) {
            // 如果工号为空，使用时间戳作为用户名
            return "user_" + System.currentTimeMillis();
        }

        String username = employeeNo;
        // 检查用户名是否已存在
        int attempt = 0;
        while (userMapper.selectByUsername(username) != null && attempt < 10) {
            // 如果工号已作为用户名使用，则添加时间戳后缀
            username = employeeNo + "_" + System.currentTimeMillis() + "_" + attempt;
            attempt++;
        }
        return username;
    }

    /**
     * 上传文件到存储服务
     */
    public String uploadFile(MultipartFile file)  {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 创建属性对象
            // AttributeImpl 构造函数参数：filePrePath（相对路径前缀，如 "default" 或 ""），fileName（文件名）
            AttributeImpl attributes = new AttributeImpl("xygpx-1", originalFilename);
            // 必须设置 fileAscription，否则会报错
            attributes.setFileAscription("default");

            // 调用存储服务上传文件
            return storageService.store(inputStream, attributes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
