package com.training.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 培训报名相关请求
 */
@Data
public class EnrollmentDTO {

    @NotNull(message = "培训ID不能为空")
    private Long trainingId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;
}

