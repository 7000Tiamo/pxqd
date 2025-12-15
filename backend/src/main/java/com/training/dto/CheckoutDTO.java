package com.training.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 签退请求参数
 */
@Data
public class CheckoutDTO {

    @NotNull(message = "培训ID不能为空")
    private Long trainingId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;
}

