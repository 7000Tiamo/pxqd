package com.training.controller;

import com.thunisoft.cloud.storage.client.core.store.IStoreService;
import com.training.common.api.Result;
import com.training.dto.LoginRequest;
import com.training.dto.TokenResponse;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.AuthService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    @Autowired
    private IStoreService storeService;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        if (token == null) {
            return Result.error("账号或密码错误/被禁用");
        }
        return Result.ok(new TokenResponse(token));
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.error("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getAvatar() != null){
            user.setAvatar(storeService.preGetObjectUrl(user.getAvatar()));
        }
        // 不返回密码
        user.setPassword(null);
        return Result.ok(user);
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("ok");
    }
}


