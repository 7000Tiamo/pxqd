package com.training.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "工号不能为空")
    private String employeeNo;
    
    private String dept;
    private String phone;
    private String role = "employee"; // admin/employee
    private String avatar; // 头像URL
}


