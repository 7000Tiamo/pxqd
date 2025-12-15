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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinMapper checkinMapper;
    private final TrainingMapper trainingMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final UserMapper userMapper;

    /**
     * 签到
     */
    @Transactional
    public Checkin checkin(Long trainingId, Long userId, Double latitude, Double longitude) {
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

        if (training.getNeedSignup() != null && training.getNeedSignup()) {
            if (!isEnrolled(trainingId, userId)) {
                throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_NOT_SIGNEDUP);
            }
        }

        Checkin existing = checkinMapper.selectByTrainingAndUser(trainingId, userId);
        if (existing != null && "signed".equals(existing.getState())) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_ALREADY_CHECKIN);
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isLate = false;
        if (training.getStartTime() != null && training.getLateMinutes() != null) {
            LocalDateTime lateThreshold = training.getStartTime().plusMinutes(training.getLateMinutes());
            isLate = now.isAfter(lateThreshold);
        }

        if (training.getGpsRequired() != null && training.getGpsRequired()) {
            if (latitude == null || longitude == null) {
                throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_NEED_GPS);
            }
        }

        if (existing == null) {
            existing = new Checkin();
            existing.setTrainingId(trainingId);
            existing.setUserId(userId);
            existing.setCreatedAt(now);
            checkinMapper.insert(existing);
        }

        existing.setCheckinTime(now);
        existing.setState("signed");
        existing.setIsLate(isLate);
        existing.setUpdatedAt(now);

        checkinMapper.updateById(existing);
        return existing;
    }

    /**
     * 签退
     */
    @Transactional
    public Checkin checkout(Long trainingId, Long userId) {
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

        if (training.getNeedCheckout() == null || !training.getNeedCheckout()) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_NOT_NEED_CHECKOUT);
        }

        Checkin checkin = checkinMapper.selectByTrainingAndUser(trainingId, userId);
        if (checkin == null || !"signed".equals(checkin.getState())) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_NEED_CHECKIN_FIRST);
        }

        if ("checked_out".equals(checkin.getState())) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, ErrorMessages.TRAINING_ALREADY_CHECKOUT);
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isEarlyLeave = false;
        if (training.getEndTime() != null && training.getEarlyLeaveMinutes() != null) {
            LocalDateTime earlyThreshold = training.getEndTime().minusMinutes(training.getEarlyLeaveMinutes());
            isEarlyLeave = now.isBefore(earlyThreshold);
        }

        checkin.setCheckoutTime(now);
        checkin.setState("checked_out");
        checkin.setIsEarlyLeave(isEarlyLeave);
        checkin.setUpdatedAt(now);

        checkinMapper.updateById(checkin);
        return checkin;
    }

    /**
     * 获取用户的签到记录
     */
    public List<Checkin> getUserCheckins(Long userId) {
        return checkinMapper.selectByUserId(userId);
    }

    /**
     * 获取培训的签到记录
     */
    public List<Checkin> getTrainingCheckins(Long trainingId) {
        return checkinMapper.selectByTrainingId(trainingId);
    }

    /**
     * 获取签到统计
     */
    public CheckinStats getCheckinStats(Long trainingId) {
        List<Checkin> all = checkinMapper.selectByTrainingId(trainingId);
        long signedCount = all.stream().filter(c -> "signed".equals(c.getState()) || "checked_out".equals(c.getState()))
                .count();
        long lateCount = all.stream().filter(c -> c.getIsLate() != null && c.getIsLate()).count();
        long checkoutCount = all.stream().filter(c -> "checked_out".equals(c.getState())).count();
        long earlyLeaveCount = all.stream().filter(c -> c.getIsEarlyLeave() != null && c.getIsEarlyLeave()).count();

        int appliedCount = enrollmentMapper.countByTrainingId(trainingId);
        double signRate = appliedCount > 0 ? (signedCount * 100.0 / appliedCount) : 0.0;

        CheckinStats stats = new CheckinStats();
        stats.setAppliedCount(appliedCount);
        stats.setSignedCount((int) signedCount);
        stats.setLateCount((int) lateCount);
        stats.setCheckoutCount((int) checkoutCount);
        stats.setEarlyLeaveCount((int) earlyLeaveCount);
        stats.setSignRate(signRate);

        return stats;
    }

    /**
     * 检查是否已报名
     */
    private boolean isEnrolled(Long trainingId, Long userId) {
        Enrollment enrollment = enrollmentMapper.selectByTrainingAndUser(trainingId, userId);
        return enrollment != null;
    }



    /**
     * 通过工号进行公开签到
     */
    public Checkin publicCheckinByEmployeeNo(Long trainingId, String employeeNo) {
        // 1. 根据工号查用户
        User user = userMapper.selectByEmployeeNo(employeeNo);

        // 2. 复用你已有的签到逻辑（传 userId）
        return checkin(trainingId, user.getId(), null, null); // latitude/longitude 暂不传
    }

    public static class CheckinStats {
        private Integer appliedCount;
        private Integer signedCount;
        private Integer lateCount;
        private Integer checkoutCount;
        private Integer earlyLeaveCount;
        private Double signRate;

        public Integer getAppliedCount() {
            return appliedCount;
        }

        public void setAppliedCount(Integer appliedCount) {
            this.appliedCount = appliedCount;
        }

        public Integer getSignedCount() {
            return signedCount;
        }

        public void setSignedCount(Integer signedCount) {
            this.signedCount = signedCount;
        }

        public Integer getLateCount() {
            return lateCount;
        }

        public void setLateCount(Integer lateCount) {
            this.lateCount = lateCount;
        }

        public Integer getCheckoutCount() {
            return checkoutCount;
        }

        public void setCheckoutCount(Integer checkoutCount) {
            this.checkoutCount = checkoutCount;
        }

        public Integer getEarlyLeaveCount() {
            return earlyLeaveCount;
        }

        public void setEarlyLeaveCount(Integer earlyLeaveCount) {
            this.earlyLeaveCount = earlyLeaveCount;
        }

        public Double getSignRate() {
            return signRate;
        }

        public void setSignRate(Double signRate) {
            this.signRate = signRate;
        }
    }
}
