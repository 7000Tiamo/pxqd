package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingUpdateDTO {
    private String title;
    private String description;
    private String trainer;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String coverUrl;
    private Boolean needSignup;
    private Boolean needCheckout;
    private Boolean gpsRequired;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;
    private Integer maxParticipants;
    private String status; // draft/open/ongoing/ended
}
