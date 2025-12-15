package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Checkin {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 培训ID
     */
    private Long trainingId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 签退时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 是否迟到
     */
    private Boolean isLate;

    /**
     * 是否早退
     */
    private Boolean isEarlyLeave;

    /**
     * 状态：not_signed/signed/checked_out
     */
    private String state;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}


