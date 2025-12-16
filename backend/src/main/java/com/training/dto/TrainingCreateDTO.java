package com.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingCreateDTO {
    @NotBlank(message = "培训名称不能为空")
    private String title;
    
    private String description;
    private String trainer;
    private String location;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    private String coverUrl;
    private Boolean needSignup = true;
    private Boolean needCheckout = false;
    private Integer lateMinutes = 15;
    private Integer earlyLeaveMinutes = 15;
    private Integer maxParticipants; // null表示不限制
}


