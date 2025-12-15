package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String name;
    private String employeeNo;
    private String dept;
    private String phone;
    private String role; // admin / employee
    private Integer status; // 1 enabled 0 disabled
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
