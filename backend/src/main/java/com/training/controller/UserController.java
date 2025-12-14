package com.training.controller;

import com.training.common.PageResult;
import com.training.common.api.ApiResponse;
import com.training.dto.UserCreateDTO;
import com.training.dto.UserUpdateDTO;
import com.training.entity.User;
import com.training.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping
    public ApiResponse<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<User> result = userService.getUserPage(page, size, keyword);
        return ApiResponse.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreateDTO dto) {
        try {
            User user = userService.createUser(dto);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try {
            boolean result = userService.updateUser(id, dto);
            return result ? ApiResponse.success(true) : ApiResponse.error("用户不存在");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除用户（软删除）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        user.setDeleted(1);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        boolean result = userService.updateUser(id, new UserUpdateDTO());
        return result ? ApiResponse.success(true) : ApiResponse.error("删除失败");
    }

    /**
     * Excel导入用户
     */
    @PostMapping("/import")
    public ApiResponse<Integer> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.importUsers(file);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * Excel导出用户
     */
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) {
        try {
            userService.exportUsers(response);
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}
