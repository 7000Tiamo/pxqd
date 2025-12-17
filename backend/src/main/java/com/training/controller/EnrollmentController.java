package com.training.controller;

import com.training.common.api.Result;
import com.training.dto.EnrollmentDTO;
import com.training.entity.Enrollment;
import com.training.service.EnrollmentService;
import com.training.vo.EnrollmentVO;
import com.training.vo.TrainingListVO;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * 报名培训
     */
    @PostMapping
    public Result<Boolean> enroll(@Valid @RequestBody EnrollmentDTO request) {
        boolean result = enrollmentService.enroll(request.getTrainingId(), request.getUserId());
        return result ? Result.success(true) : Result.error("报名失败");
    }

    /**
     * 取消报名
     */
    @PostMapping("/cancel")
    public Result<Boolean> cancelEnrollment(@Valid @RequestBody EnrollmentDTO request) {
        boolean result = enrollmentService.cancelEnrollment(request.getTrainingId(), request.getUserId());
        return result ? Result.success(true) : Result.error("取消失败");
    }

    /**
     * 获取用户的报名列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Enrollment>> getUserEnrollments(@PathVariable Long userId) {
        List<Enrollment> list = enrollmentService.getUserEnrollments(userId);
        return Result.success(list);
    }

    /**
     * 获取培训的报名列表
     */
    @GetMapping("/training/{trainingId}")
    public Result<List<EnrollmentVO>> getTrainingEnrollments(@PathVariable Long trainingId) {
        List<EnrollmentVO> voList = enrollmentService.getTrainingEnrollmentVOs(trainingId);
        return Result.success(voList);
    }

    /**
     * 检查是否已报名
     */
    @GetMapping("/check")
    public Result<Boolean> checkEnrollment(
            @RequestParam Long trainingId,
            @RequestParam Long userId) {
        boolean enrolled = enrollmentService.isEnrolled(trainingId, userId);
        return Result.success(enrolled);
    }

    /**
     * 获取用户已报名的培训列表（包含培训详情）
     * @param userId 用户ID
     * @param status 培训状态过滤（可选）：open-报名中，ongoing-进行中，ended-已结束
     */
    @GetMapping("/user/{userId}/trainings")
    public Result<List<TrainingListVO>> getUserEnrolledTrainings(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        List<TrainingListVO> list = enrollmentService.getUserEnrolledTrainings(userId, status);
        return Result.success(list);
    }
}


