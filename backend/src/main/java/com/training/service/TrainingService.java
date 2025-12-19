package com.training.service;

import com.training.common.PageResult;
import com.training.dto.TrainingCreateDTO;
import com.training.dto.TrainingUpdateDTO;
import com.training.entity.Checkin;
import com.training.common.api.BizException;
import com.training.common.api.ErrorCode;
import com.training.common.api.ErrorMessages;
import com.training.entity.Training;
import com.training.mapper.CheckinMapper;
import com.training.mapper.EnrollmentMapper;
import com.training.mapper.TrainingMapper;
import com.training.util.DateTimeUtil;
import com.training.vo.TrainingDetailVO;
import com.training.vo.TrainingListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingMapper trainingMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final CheckinMapper checkinMapper;

    /**
     * 创建培训（草稿状态）
     */
    @Transactional
    public Training createTraining(TrainingCreateDTO dto) {
        // 验证结束时间必须大于开始时间
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (!dto.getEndTime().isAfter(dto.getStartTime())) {
                throw new BizException(ErrorCode.BUSINESS_CONFLICT, "结束时间必须大于开始时间");
            }
        }

        Training training = new Training();
        training.setTitle(dto.getTitle());
        training.setDescription(dto.getDescription());
        training.setTrainer(dto.getTrainer());
        training.setLocation(dto.getLocation());
        training.setStartTime(dto.getStartTime());
        training.setEndTime(dto.getEndTime());
        training.setCoverUrl(dto.getCoverUrl());
        training.setStatus("draft");
        training.setNeedSignup(dto.getNeedSignup() != null ? dto.getNeedSignup() : true);
        training.setNeedCheckout(dto.getNeedCheckout() != null ? dto.getNeedCheckout() : false);
        training.setLateMinutes(dto.getLateMinutes() != null ? dto.getLateMinutes() : 15);
        training.setEarlyLeaveMinutes(dto.getEarlyLeaveMinutes() != null ? dto.getEarlyLeaveMinutes() : 15);
        training.setMaxParticipants(dto.getMaxParticipants());
        training.setCreatedAt(DateTimeUtil.now());
        training.setUpdatedAt(DateTimeUtil.now());

        trainingMapper.insert(training);
        return training;
    }

    /**
     * 更新培训
     */
    @Transactional
    public boolean updateTraining(Long id, TrainingUpdateDTO dto) {
        Training training = trainingMapper.selectById(id);
        if (training == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.TRAINING_NOT_FOUND);
        }

        if (dto.getTitle() != null)
            training.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            training.setDescription(dto.getDescription());
        if (dto.getTrainer() != null)
            training.setTrainer(dto.getTrainer());
        if (dto.getLocation() != null)
            training.setLocation(dto.getLocation());
        if (dto.getStartTime() != null)
            training.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null)
            training.setEndTime(dto.getEndTime());
        if (dto.getCoverUrl() != null)
            training.setCoverUrl(dto.getCoverUrl());
        if (dto.getNeedSignup() != null)
            training.setNeedSignup(dto.getNeedSignup());
        if (dto.getNeedCheckout() != null)
            training.setNeedCheckout(dto.getNeedCheckout());
        if (dto.getLateMinutes() != null)
            training.setLateMinutes(dto.getLateMinutes());
        if (dto.getEarlyLeaveMinutes() != null)
            training.setEarlyLeaveMinutes(dto.getEarlyLeaveMinutes());
        if (dto.getMaxParticipants() != null)
            training.setMaxParticipants(dto.getMaxParticipants());
        if (dto.getStatus() != null)
            training.setStatus(dto.getStatus());

        // 验证结束时间必须大于开始时间
        LocalDateTime startTime = training.getStartTime();
        LocalDateTime endTime = training.getEndTime();
        if (startTime != null && endTime != null) {
            if (!endTime.isAfter(startTime)) {
                throw new BizException(ErrorCode.BUSINESS_CONFLICT, "结束时间必须大于开始时间");
            }
        }

        training.setUpdatedAt(DateTimeUtil.now());
        return trainingMapper.updateById(training) > 0;
    }

    /**
     * 发布培训
     */
    @Transactional
    public boolean publishTraining(Long id) {
        Training training = trainingMapper.selectById(id);
        if (training == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.TRAINING_NOT_FOUND);
        }

        if (training.getStartTime() == null || training.getEndTime() == null) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_TIME_NOT_SET);
        }

        // 验证结束时间必须大于开始时间
        if (!training.getEndTime().isAfter(training.getStartTime())) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, "结束时间必须大于开始时间");
        }

        training.setStatus("open");
        training.setUpdatedAt(DateTimeUtil.now());
        return trainingMapper.updateById(training) > 0;
    }

    /**
     * 分页查询培训列表
     */
    public PageResult<TrainingListVO> getTrainingPage(Integer page, Integer size, String keyword, String status) {
        List<Training> all = trainingMapper.selectByCondition(keyword, status);
        long total = all.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, all.size());
        List<Training> records = start < all.size() ? all.subList(start, end) : Collections.emptyList();
        
        // 转换为 TrainingListVO 并添加报名人数
        List<TrainingListVO> voList = records.stream().map(training -> {
            TrainingListVO vo = new TrainingListVO();
            vo.setId(training.getId());
            vo.setTitle(training.getTitle());
            vo.setDescription(training.getDescription());
            vo.setTrainer(training.getTrainer());
            vo.setLocation(training.getLocation());
            vo.setStartTime(training.getStartTime());
            vo.setEndTime(training.getEndTime());
            vo.setCoverUrl(training.getCoverUrl());
            vo.setStatus(training.getStatus());
            vo.setNeedSignup(training.getNeedSignup());
            vo.setNeedCheckout(training.getNeedCheckout());
            vo.setLateMinutes(training.getLateMinutes());
            vo.setEarlyLeaveMinutes(training.getEarlyLeaveMinutes());
            vo.setMaxParticipants(training.getMaxParticipants());
            vo.setCreatedAt(training.getCreatedAt());
            vo.setUpdatedAt(training.getUpdatedAt());
            
            // 获取报名人数
            int appliedCount = enrollmentMapper.countByTrainingId(training.getId());
            vo.setAppliedCount(appliedCount);
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, size);
    }

    /**
     * 获取培训详情（包含统计信息）
     */
    public TrainingDetailVO getTrainingDetail(Long id) {
        Training training = trainingMapper.selectById(id);
        if (training == null) {
            return null;
        }

        TrainingDetailVO vo = new TrainingDetailVO();
        vo.setId(training.getId());
        vo.setTitle(training.getTitle());
        vo.setDescription(training.getDescription());
        vo.setTrainer(training.getTrainer());
        vo.setLocation(training.getLocation());
        vo.setStartTime(training.getStartTime());
        vo.setEndTime(training.getEndTime());
        vo.setCoverUrl(training.getCoverUrl());
        vo.setStatus(training.getStatus());
        vo.setNeedSignup(training.getNeedSignup());
        vo.setNeedCheckout(training.getNeedCheckout());
        vo.setLateMinutes(training.getLateMinutes());
        vo.setEarlyLeaveMinutes(training.getEarlyLeaveMinutes());
        vo.setMaxParticipants(training.getMaxParticipants());

        // 统计信息
        int appliedCount = enrollmentMapper.countByTrainingId(id);
        vo.setAppliedCount(appliedCount);

        // 签到统计
        List<Checkin> checkins = checkinMapper.selectByTrainingId(id);
        long signedCount = checkins.stream()
                .filter(c -> "signed".equals(c.getState()) || "checked_out".equals(c.getState()))
                .count();
        long lateCount = checkins.stream()
                .filter(c -> c.getIsLate() != null && c.getIsLate())
                .count();
        long checkoutCount = checkins.stream()
                .filter(c -> "checked_out".equals(c.getState()))
                .count();

        double signRate = appliedCount > 0 ? (signedCount * 100.0 / appliedCount) : 0.0;

        vo.setSignedCount((int) signedCount);
        vo.setLateCount((int) lateCount);
        vo.setCheckoutCount((int) checkoutCount);
        vo.setSignRate(signRate);

        return vo;
    }

    /**
     * 获取培训
     */
    public Training getById(Long id) {
        return trainingMapper.selectById(id);
    }

    /**
     * 获取所有培训
     */
    public List<Training> list() {
        return trainingMapper.selectAll();
    }

    /**
     * 自动更新培训状态（定时任务调用）
     * 只更新已发布（open）和进行中（ongoing）状态的培训
     * 草稿（draft）状态由管理员手动管理，不自动更新
     */
    @Transactional
    public void updateTrainingStatus() {
        LocalDateTime now = DateTimeUtil.now();
        // 只查询已发布（报名中）和进行中的培训
        List<Training> trainings = trainingMapper.selectByStatus("open");
        trainings.addAll(trainingMapper.selectByStatus("ongoing"));

        for (Training training : trainings) {
            if (training.getStartTime() != null && training.getEndTime() != null) {
                // 如果当前时间在开始时间和结束时间之间，且状态不是"进行中"，则更新为"进行中"
                if (now.isAfter(training.getStartTime()) && now.isBefore(training.getEndTime())) {
                    if (!"ongoing".equals(training.getStatus())) {
                        training.setStatus("ongoing");
                        training.setUpdatedAt(now);
                        trainingMapper.updateById(training);
                    }
                } 
                // 如果当前时间已超过结束时间，且状态不是"已结束"，则更新为"已结束"
                else if (now.isAfter(training.getEndTime())) {
                    if (!"ended".equals(training.getStatus())) {
                        training.setStatus("ended");
                        training.setUpdatedAt(now);
                        trainingMapper.updateById(training);
                    }
                }
            }
        }
    }

    /**
     * 删除培训
     */
    @Transactional
    public boolean deleteTraining(Long id) {
        Training training = trainingMapper.selectById(id);
        if (training == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.TRAINING_NOT_FOUND);
        }
        return trainingMapper.deleteById(id) > 0;
    }
}
