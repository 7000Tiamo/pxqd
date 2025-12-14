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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
        if (userMapper.countByEmployeeNo(dto.getEmployeeNo(), null) > 0) {
            throw new RuntimeException("工号已存在");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmployeeNo(dto.getEmployeeNo());
        user.setDept(dto.getDept());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? dto.getRole() : "employee");
        user.setStatus(1);
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(0);

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
        List<User> records = start < all.size() ? all.subList(start, end) : List.of();
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

            User user = new User();
            user.setName(excel.getName());
            user.setEmployeeNo(excel.getEmployeeNo());
            user.setDept(excel.getDept());
            user.setPhone(excel.getPhone());
            user.setRole("employee");
            user.setStatus(1);
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setDeleted(0);

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
        String fileName = URLEncoder.encode("用户列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), UserExcel.class)
                .sheet("用户列表")
                .doWrite(excelList);
    }

    /**
     * 删除用户（软删除）
     */
    @Transactional
    public boolean deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }
        user.setDeleted(1);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }
}
