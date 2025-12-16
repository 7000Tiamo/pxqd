package com.training.vo;

import java.time.LocalDateTime;
import lombok.Data;

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
    private LocalDateTime checkoutTime;
    private Boolean isLate;
    private Boolean isEarlyLeave;
}

