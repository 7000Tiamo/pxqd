package com.training.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcel {
    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    private String username;

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

    /**
     * 角色
     */
    @ExcelProperty("角色")
    private String role;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createdAt;
}


