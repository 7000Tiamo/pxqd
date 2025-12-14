package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentVO {
    private Long id;
    private Long trainingId;
    private Long userId;
    private String userName;
    private String userDept;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime checkinTime;
    private Boolean isLate;
}

