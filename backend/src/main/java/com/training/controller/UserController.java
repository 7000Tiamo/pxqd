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
    @PostMapping(consumes = {"multipart/form-data"})
    public Result<User> createUser(
            @RequestPart("userData") @Valid UserCreateDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {
            User user = userService.createUserWithAvatar(dto, avatarFile);
            return Result.success(user);
    }

    /**
     * 更新用户（支持头像上传）
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public Result<Boolean> updateUser(
            @PathVariable Long id,
            @RequestPart("userData") @Valid UserUpdateDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {
        boolean result = userService.updateUserWithAvatar(id, dto, avatarFile);
        return result ? Result.success(true) : Result.error("用户不存在");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        return result ? Result.success(true) : Result.error("用户不存在");
    }

    /**
     * Excel导入用户
     */
    @PostMapping("/import")
    public Result<Integer> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        int count = userService.importUsers(file);
        return Result.success(count);
    }

    /**
     * Excel导出用户
     * @param keyword 关键词筛选（可选）
     */
    @GetMapping("/export")
    public void exportUsers(
            HttpServletResponse response,
            @RequestParam(required = false) String keyword) throws IOException {
        userService.exportUsers(response, keyword);
    }
}
