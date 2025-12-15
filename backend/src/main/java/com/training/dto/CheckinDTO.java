package com.training.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 签到请求参数
 */
@Data
public class CheckinDTO {

    @NotNull(message = "培训ID不能为空")
    private Long trainingId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 纬度，可选
     */
    private Double latitude;

    /**
     * 经度，可选
     */
    private Double longitude;
}

