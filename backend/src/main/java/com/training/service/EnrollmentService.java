package com.training.service;

import com.training.entity.Enrollment;
import com.training.entity.Training;
import com.training.entity.User;
import com.training.mapper.EnrollmentMapper;
import com.training.mapper.TrainingMapper;
import com.training.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final TrainingMapper trainingMapper;
    private final UserMapper userMapper;

    /**
     * 报名培训
     */
    @Transactional
    public boolean enroll(Long trainingId, Long userId) {
        // 校验培训是否存在
        Training training = trainingMapper.selectById(trainingId);
        if (training == null) {
            throw new RuntimeException("培训不存在");
        }

        // 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (training.getNeedSignup() == null || !training.getNeedSignup()) {
            throw new RuntimeException("该培训不需要报名");
        }

        Enrollment existing = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        if (existing != null) {
            throw new RuntimeException("您已报名该培训");
        }

        if (training.getMaxParticipants() != null && training.getMaxParticipants() > 0) {
            int enrolledCount = enrollmentMapper.countByTrainingId(trainingId);
            if (enrolledCount >= training.getMaxParticipants()) {
                throw new RuntimeException("报名人数已满");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setTrainingId(trainingId);
        enrollment.setUserId(userId);
        enrollment.setStatus("enrolled");
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setCreatedAt(LocalDateTime.now());

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
            throw new RuntimeException("培训不存在");
        }

        // 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Enrollment enrollment = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        if (enrollment == null) {
            throw new RuntimeException("未找到报名记录");
        }

        enrollment.setStatus("cancelled");
        enrollment.setCancelledAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
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
     * 检查用户是否已报名
     */
    public boolean isEnrolled(Long trainingId, Long userId) {
        Enrollment enrollment = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        return enrollment != null;
    }
}
