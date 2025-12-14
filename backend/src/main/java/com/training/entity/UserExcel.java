package com.training.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcel {
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("工号")
    private String employeeNo;
    
    @ExcelProperty("部门")
    private String dept;
    
    @ExcelProperty("手机号")
    private String phone;
}


