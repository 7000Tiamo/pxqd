package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Enrollment {
    private Long id;
    private Long trainingId;
    private Long userId;
    private String status; // enrolled / cancelled
    private LocalDateTime enrolledAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}


