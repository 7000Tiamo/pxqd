package com.training.controller;

import com.training.common.api.ApiResponse;
import com.training.dto.LoginRequest;
import com.training.dto.TokenResponse;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        if (token == null) {
            return ApiResponse.error("账号或密码错误/被禁用");
        }
        return ApiResponse.ok(new TokenResponse(token));
    }

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        if (userId == null) {
            return ApiResponse.error("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("ok");
    }
}


