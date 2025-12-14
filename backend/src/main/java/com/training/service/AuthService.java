package com.training.service;

import com.training.dto.LoginRequest;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest req) {
        User user = userMapper.selectByUsername(req.getUsername());
        if (user == null) {
            return null;
        }
        String encoded = DigestUtils.md5DigestAsHex(req.getPassword().getBytes());
        if (!encoded.equals(user.getPassword()) || user.getStatus() != 1) {
            return null;
        }
        return jwtUtil.generateToken(user.getId());
    }

    public Long parseToken(String token) {
        return jwtUtil.parse(token);
    }
}
