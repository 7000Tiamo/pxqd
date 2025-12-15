package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Training {
    private Long id;
    private String title;
    private String description;
    private String trainer;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String coverUrl;
    private String status; // draft/open/ongoing/ended
    private Boolean needSignup;
    private Boolean needCheckout;
    private Boolean gpsRequired;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
