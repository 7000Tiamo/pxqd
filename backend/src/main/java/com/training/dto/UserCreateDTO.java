package com.training.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "工号不能为空")
    private String employeeNo;
    
    private String dept;
    private String phone;
    private String role = "employee"; // admin/employee
}


