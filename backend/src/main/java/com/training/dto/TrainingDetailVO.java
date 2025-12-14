package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingDetailVO {
    private Long id;
    private String title;
    private String description;
    private String trainer;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String coverUrl;
    private String status;
    private Boolean needSignup;
    private Boolean needCheckout;
    private Boolean gpsRequired;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;
    private Integer maxParticipants;
    
    // 统计信息
    private Integer appliedCount; // 已报名人数
    private Integer signedCount; // 已签到人数
    private Integer lateCount; // 迟到人数
    private Integer checkoutCount; // 已签退人数
    private Double signRate; // 签到率
}


