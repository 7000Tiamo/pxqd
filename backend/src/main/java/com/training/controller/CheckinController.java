package com.training.controller;

import com.training.common.api.Result;
import com.training.dto.CheckinDTO;
import com.training.dto.CheckoutDTO;
import com.training.entity.Checkin;
import com.training.service.CheckinService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    /**
     * 签到
     */
    @PostMapping
    public Result<Checkin> checkin(@Valid @RequestBody CheckinDTO request) {
        Checkin checkin = checkinService.checkin(
                request.getTrainingId(),
                request.getUserId(),
                request.getLatitude(),
                request.getLongitude());
        return Result.success(checkin);
    }

    /**
     * 签退
     */
    @PostMapping("/checkout")
    public Result<Checkin> checkout(@Valid @RequestBody CheckoutDTO request) {
        Checkin checkin = checkinService.checkout(request.getTrainingId(), request.getUserId());
        return Result.success(checkin);
    }

    /**
     * 获取用户的签到记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<Checkin>> getUserCheckins(@PathVariable Long userId) {
        List<Checkin> list = checkinService.getUserCheckins(userId);
        return Result.success(list);
    }

    /**
     * 获取培训的签到记录
     */
    @GetMapping("/training/{trainingId}")
    public Result<List<Checkin>> getTrainingCheckins(@PathVariable Long trainingId) {
        List<Checkin> list = checkinService.getTrainingCheckins(trainingId);
        return Result.success(list);
    }

    /**
     * 获取签到统计
     */
    @GetMapping("/stats/{trainingId}")
    public Result<CheckinService.CheckinStats> getCheckinStats(@PathVariable Long trainingId) {
        CheckinService.CheckinStats stats = checkinService.getCheckinStats(trainingId);
        return Result.success(stats);
    }
}


