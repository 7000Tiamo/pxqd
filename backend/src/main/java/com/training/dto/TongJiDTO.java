package com.training.dto;

import lombok.Data;

/**
 * 数据统计 DTO - 仅包含三大核心指标
 */
@Data
public class TongJiDTO {

    /**
     * 本月平均参与率（单位：百分比，如 95.5 表示 95.5%）
     */
    private double monthlyParticipationRate;

    /**
     * 累计培训时长（单位：小时，如 126.0）
     */
    private double totalTrainingHours;

    /**
     * 人均培训场次（如 3.2）
     */
    private double averageTrainingSessions;
}
