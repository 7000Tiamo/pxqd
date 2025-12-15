package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Enrollment {
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
     * 状态：enrolled/cancelled
     */
    private String status;

    /**
     * 报名时间
     */
    private LocalDateTime enrolledAt;

    /**
     * 取消报名时间
     */
    private LocalDateTime cancelledAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}


