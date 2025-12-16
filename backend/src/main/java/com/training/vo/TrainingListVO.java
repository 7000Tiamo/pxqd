package com.training.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TrainingListVO {
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
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 报名人数（列表需要）
    private Integer appliedCount;
}

