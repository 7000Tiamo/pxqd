package com.training.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.training.common.PageResult;
import com.training.dto.UserCreateDTO;
import com.training.dto.UserUpdateDTO;
import com.training.entity.User;
import com.training.entity.UserExcel;
import com.training.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

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
        // 使用MD5加密密码
        String encodedPassword = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user;
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
            String username = generateUniqueUsername(excel.getEmployeeNo());

            User user = new User();
            user.setUsername(username);
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
     */
    public void exportUsers(HttpServletResponse response) throws IOException {
        List<User> users = userMapper.selectAll();
        List<UserExcel> excelList = users.stream().map(user -> {
            UserExcel excel = new UserExcel();
            excel.setName(user.getName());
            excel.setEmployeeNo(user.getEmployeeNo());
            excel.setDept(user.getDept());
            excel.setPhone(user.getPhone());
            return excel;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), UserExcel.class)
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
}
