package com.training.controller;

import com.training.common.api.ApiResponse;
import com.training.entity.Checkin;
import com.training.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
public class CheckinController {
    
    private final CheckinService checkinService;
    
    /**
     * 签到
     */
    @PostMapping
    public ApiResponse<Checkin> checkin(@RequestBody Map<String, Object> request) {
        Long trainingId = Long.valueOf(request.get("trainingId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Double latitude = request.get("latitude") != null ? Double.valueOf(request.get("latitude").toString()) : null;
        Double longitude = request.get("longitude") != null ? Double.valueOf(request.get("longitude").toString()) : null;
        
        try {
            Checkin checkin = checkinService.checkin(trainingId, userId, latitude, longitude);
            return ApiResponse.success(checkin);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 签退
     */
    @PostMapping("/checkout")
    public ApiResponse<Checkin> checkout(@RequestBody Map<String, Long> request) {
        Long trainingId = request.get("trainingId");
        Long userId = request.get("userId");
        
        if (trainingId == null || userId == null) {
            return ApiResponse.error("参数错误");
        }
        
        try {
            Checkin checkin = checkinService.checkout(trainingId, userId);
            return ApiResponse.success(checkin);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的签到记录
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Checkin>> getUserCheckins(@PathVariable Long userId) {
        List<Checkin> list = checkinService.getUserCheckins(userId);
        return ApiResponse.success(list);
    }
    
    /**
     * 获取培训的签到记录
     */
    @GetMapping("/training/{trainingId}")
    public ApiResponse<List<Checkin>> getTrainingCheckins(@PathVariable Long trainingId) {
        List<Checkin> list = checkinService.getTrainingCheckins(trainingId);
        return ApiResponse.success(list);
    }
    
    /**
     * 获取签到统计
     */
    @GetMapping("/stats/{trainingId}")
    public ApiResponse<CheckinService.CheckinStats> getCheckinStats(@PathVariable Long trainingId) {
        CheckinService.CheckinStats stats = checkinService.getCheckinStats(trainingId);
        return ApiResponse.success(stats);
    }



    /**
     * 公开扫码签到（通过手机号或工号）
     */
    @PostMapping("/public")
    public ApiResponse<Checkin> publicCheckin(@RequestBody Map<String, String> request) {
        String trainingIdStr = request.get("trainingId");
        String identifier = request.get("employeeNo"); // 手机号 或 工号

        if (trainingIdStr == null || identifier == null || identifier.trim().isEmpty()) {
            return ApiResponse.error("培训ID和手机号/工号不能为空");
        }

        Long trainingId;
        try {
            trainingId = Long.valueOf(trainingIdStr);
        } catch (NumberFormatException e) {
            return ApiResponse.error("无效的培训ID");
        }

        try {
            Checkin checkin = checkinService.publicCheckinByEmployeeNo(trainingId, identifier);
            return ApiResponse.success(checkin);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


