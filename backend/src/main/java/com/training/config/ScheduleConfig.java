package com.training.config;

import com.training.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务配置
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {

    private final TrainingService trainingService;

    /**
     * 自动更新培训状态
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateTrainingStatus() {
        try {
            trainingService.updateTrainingStatus();
            log.debug("培训状态自动更新完成");
        } catch (Exception e) {
            log.error("培训状态自动更新失败", e);
        }
    }
}

