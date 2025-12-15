package com.training.controller;

import com.training.common.PageResult;
import com.training.common.api.Result;
import com.training.dto.UserCreateDTO;
import com.training.dto.UserUpdateDTO;
import com.training.entity.User;
import com.training.service.UserService;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<User> result = userService.getUserPage(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody UserCreateDTO dto) {
        try {
            User user = userService.createUser(dto);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try {
            boolean result = userService.updateUser(id, dto);
            return result ? Result.success(true) : Result.error("用户不存在");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.deleteUser(id);
            return result ? Result.success(true) : Result.error("用户不存在");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * Excel导入用户
     */
    @PostMapping("/import")
    public Result<Integer> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.importUsers(file);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
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
