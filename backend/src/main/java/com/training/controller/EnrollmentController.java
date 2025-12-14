package com.training.controller;

import com.training.common.api.ApiResponse;
import com.training.dto.EnrollmentVO;
import com.training.entity.Enrollment;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.CheckinService;
import com.training.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    private final UserMapper userMapper;
    private final CheckinService checkinService;
    
    /**
     * 报名培训
     */
    @PostMapping
    public ApiResponse<Boolean> enroll(@RequestBody Map<String, Long> request) {
        Long trainingId = request.get("trainingId");
        Long userId = request.get("userId");
        
        if (trainingId == null || userId == null) {
            return ApiResponse.error("参数错误");
        }
        
        try {
            boolean result = enrollmentService.enroll(trainingId, userId);
            return result ? ApiResponse.success(true) : ApiResponse.error("报名失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 取消报名
     */
    @PostMapping("/cancel")
    public ApiResponse<Boolean> cancelEnrollment(@RequestBody Map<String, Long> request) {
        Long trainingId = request.get("trainingId");
        Long userId = request.get("userId");
        
        if (trainingId == null || userId == null) {
            return ApiResponse.error("参数错误");
        }
        
        try {
            boolean result = enrollmentService.cancelEnrollment(trainingId, userId);
            return result ? ApiResponse.success(true) : ApiResponse.error("取消失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的报名列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Enrollment>> getUserEnrollments(@PathVariable Long userId) {
        List<Enrollment> list = enrollmentService.getUserEnrollments(userId);
        return ApiResponse.success(list);
    }
    
    /**
     * 获取培训的报名列表
     */
    @GetMapping("/training/{trainingId}")
    public ApiResponse<List<EnrollmentVO>> getTrainingEnrollments(@PathVariable Long trainingId) {
        List<Enrollment> enrollments = enrollmentService.getTrainingEnrollments(trainingId);
        List<EnrollmentVO> voList = enrollments.stream().map(enrollment -> {
            EnrollmentVO vo = new EnrollmentVO();
            vo.setId(enrollment.getId());
            vo.setTrainingId(enrollment.getTrainingId());
            vo.setUserId(enrollment.getUserId());
            vo.setStatus(enrollment.getStatus());
            vo.setEnrolledAt(enrollment.getEnrolledAt());
            
            // 获取用户信息
            User user = userMapper.selectById(enrollment.getUserId());
            if (user != null) {
                vo.setUserName(user.getName());
                vo.setUserDept(user.getDept());
            }
            
            // 获取签到信息
            List<com.training.entity.Checkin> checkins = checkinService.getTrainingCheckins(trainingId);
            checkins.stream()
                    .filter(c -> c.getUserId().equals(enrollment.getUserId()))
                    .findFirst()
                    .ifPresent(checkin -> {
                        vo.setCheckinTime(checkin.getCheckinTime());
                        vo.setIsLate(checkin.getIsLate());
                    });
            
            return vo;
        }).collect(Collectors.toList());
        return ApiResponse.success(voList);
    }
    
    /**
     * 检查是否已报名
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> checkEnrollment(
            @RequestParam Long trainingId,
            @RequestParam Long userId) {
        boolean enrolled = enrollmentService.isEnrolled(trainingId, userId);
        return ApiResponse.success(enrolled);
    }
}


