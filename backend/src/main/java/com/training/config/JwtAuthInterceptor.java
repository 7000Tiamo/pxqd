package com.training.config;

import com.training.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public JwtAuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        // 排除登录和ping接口（支持/api前缀）
        if (uri.equals("/auth/login") || uri.equals("/auth/ping") 
            || uri.equals("/api/auth/login") || uri.equals("/api/auth/ping")) {
            return true;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String token = authHeader.substring(7);
        Long userId = authService.parseToken(token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        request.setAttribute("currentUserId", userId);
        return true;
    }
}


