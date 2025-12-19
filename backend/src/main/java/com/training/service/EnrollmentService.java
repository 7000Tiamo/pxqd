package com.training.service;

import com.training.common.api.BizException;
import com.training.common.api.ErrorCode;
import com.training.common.api.ErrorMessages;
import com.training.entity.Checkin;
import com.training.entity.Enrollment;
import com.training.entity.Training;
import com.training.entity.User;
import com.training.mapper.CheckinMapper;
import com.training.mapper.EnrollmentMapper;
import com.training.mapper.TrainingMapper;
import com.training.mapper.UserMapper;
import com.training.vo.EnrollmentVO;
import com.training.vo.TrainingListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.util.DateTimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final TrainingMapper trainingMapper;
    private final UserMapper userMapper;
    private final CheckinMapper checkinMapper;

    /**
     * 报名培训
     */
    @Transactional
    public boolean enroll(Long trainingId, Long userId) {
        // 校验培训是否存在
        Training training = trainingMapper.selectById(trainingId);
        if (training == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.TRAINING_NOT_FOUND);
        }

        // 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.USER_NOT_FOUND);
        }

        if (training.getNeedSignup() == null || !training.getNeedSignup()) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_NOT_NEED_SIGNUP);
        }

        Enrollment existing = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        if (existing != null) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_ALREADY_SIGNEDUP);
        }

        if (training.getMaxParticipants() != null && training.getMaxParticipants() > 0) {
            int enrolledCount = enrollmentMapper.countByTrainingId(trainingId);
            if (enrolledCount >= training.getMaxParticipants()) {
                throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_FULL);
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setTrainingId(trainingId);
        enrollment.setUserId(userId);
        enrollment.setStatus("enrolled");
        enrollment.setEnrolledAt(DateTimeUtil.now());
        enrollment.setCreatedAt(DateTimeUtil.now());

        return enrollmentMapper.insert(enrollment) > 0;
    }

    /**
     * 取消报名
     */
    @Transactional
    public boolean cancelEnrollment(Long trainingId, Long userId) {
        // 校验培训是否存在
        Training training = trainingMapper.selectById(trainingId);
        if (training == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.TRAINING_NOT_FOUND);
        }

        // 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.USER_NOT_FOUND);
        }

        Enrollment enrollment = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        if (enrollment == null) {
            throw new BizException(ErrorCode.NOT_FOUND, ErrorMessages.ENROLLMENT_NOT_FOUND);
        }

        enrollment.setStatus("cancelled");
        enrollment.setCancelledAt(DateTimeUtil.now());
        enrollment.setUpdatedAt(DateTimeUtil.now());
        return enrollmentMapper.updateById(enrollment) > 0;
    }

    /**
     * 获取用户的报名列表
     */
    public List<Enrollment> getUserEnrollments(Long userId) {
        return enrollmentMapper.selectByUserId(userId);
    }

    /**
     * 获取培训的报名列表
     */
    public List<Enrollment> getTrainingEnrollments(Long trainingId) {
        return enrollmentMapper.selectByTrainingId(trainingId);
    }

    /**
     * 获取培训报名列表（含用户与签到信息）
     */
    public List<EnrollmentVO> getTrainingEnrollmentVOs(Long trainingId) {
        List<Enrollment> enrollments = getTrainingEnrollments(trainingId);
        List<Checkin> checkins = checkinMapper.selectByTrainingId(trainingId);

        return enrollments.stream().map(enrollment -> {
            EnrollmentVO vo = new EnrollmentVO();
            vo.setId(enrollment.getId());
            vo.setTrainingId(enrollment.getTrainingId());
            vo.setUserId(enrollment.getUserId());
            vo.setStatus(enrollment.getStatus());
            vo.setEnrolledAt(enrollment.getEnrolledAt());

            User user = userMapper.selectById(enrollment.getUserId());
            if (user != null) {
                vo.setUserName(user.getName());
                vo.setUserDept(user.getDept());
            }

            checkins.stream()
                    .filter(c -> c.getUserId().equals(enrollment.getUserId()))
                    .findFirst()
                    .ifPresent(checkin -> {
                        vo.setCheckinTime(checkin.getCheckinTime());
                        vo.setCheckoutTime(checkin.getCheckoutTime());
                        vo.setIsLate(checkin.getIsLate());
                        vo.setIsEarlyLeave(checkin.getIsEarlyLeave());
                    });

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 检查用户是否已报名
     */
    public boolean isEnrolled(Long trainingId, Long userId) {
        Enrollment enrollment = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        return enrollment != null;
    }

    /**
     * 获取用户已报名的培训列表（包含培训详情）
     * @param userId 用户ID
     * @param trainingStatus 培训状态过滤（可选）：open-报名中，ongoing-进行中，ended-已结束
     * @return 培训列表
     */
    public List<TrainingListVO> getUserEnrolledTrainings(Long userId, String trainingStatus) {
        List<Enrollment> enrollments = enrollmentMapper.selectByUserId(userId);
        
        return enrollments.stream()
                .map(enrollment -> {
                    Training training = trainingMapper.selectById(enrollment.getTrainingId());
                    if (training == null) {
                        return null;
                    }
                    
                    // 如果指定了培训状态过滤，则进行过滤
                    if (trainingStatus != null && !trainingStatus.isEmpty() 
                        && !trainingStatus.equals(training.getStatus())) {
                        return null;
                    }
                    
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
                })
                .filter(vo -> vo != null)
                .sorted((a, b) -> {
                    // 按开始时间倒序排列
                    if (a.getStartTime() != null && b.getStartTime() != null) {
                        return b.getStartTime().compareTo(a.getStartTime());
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }
}
