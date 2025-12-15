package com.training.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcel {
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;
    
    /**
     * 工号
     */
    @ExcelProperty("工号")
    private String employeeNo;
    
    /**
     * 部门
     */
    @ExcelProperty("部门")
    private String dept;
    
    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    private String phone;
}


