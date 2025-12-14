package com.training.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String name;
    private String employeeNo;
    private String dept;
    private String phone;
    private Integer status; // 0禁用 1启用
}


