package com.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公开扫码签退请求参数
 */
@Data
public class PublicCheckoutDTO {

    @NotNull(message = "培训ID不能为空")
    private Long trainingId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "工号不能为空")
    private String employeeNo;

    @NotBlank(message = "token不能为空")
    private  String token;

}

