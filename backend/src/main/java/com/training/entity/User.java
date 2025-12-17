package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 部门
     */
    private String dept;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色：admin/employee
     */
    private String role;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 登录密码（存储散列后）
     */
    private String password;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
