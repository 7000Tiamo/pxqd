package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Checkin {
    private Long id;
    private Long trainingId;
    private Long userId;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private Boolean isLate;
    private Boolean isEarlyLeave;
    private String state; // not_signed / signed / checked_out
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


