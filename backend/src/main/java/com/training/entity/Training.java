package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Training {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 培训标题
     */
    private String title;

    /**
     * 培训描述
     */
    private String description;

    /**
     * 讲师/培训负责人
     */
    private String trainer;

    /**
     * 培训地点
     */
    private String location;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 状态：draft/open/ongoing/ended
     */
    private String status;

    /**
     * 是否需要报名
     */
    private Boolean needSignup;

    /**
     * 是否需要签退
     */
    private Boolean needCheckout;

    /**
     * 迟到容忍分钟数
     */
    private Integer lateMinutes;

    /**
     * 早退阈值分钟数
     */
    private Integer earlyLeaveMinutes;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
