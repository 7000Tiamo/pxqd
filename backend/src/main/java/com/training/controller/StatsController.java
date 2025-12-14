package com.training.controller;

import com.training.common.api.ApiResponse;
import com.training.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    
    private final StatsService statsService;
    
    /**
     * 获取首页统计数据
     */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = statsService.getOverviewStats();
        return ApiResponse.success(stats);
    }
    
    /**
     * 按培训项目统计
     */
    @GetMapping("/by-training")
    public ApiResponse<List<Map<String, Object>>> getStatsByTraining() {
        List<Map<String, Object>> result = statsService.getStatsByTraining();
        return ApiResponse.success(result);
    }
    
    /**
     * 按部门统计
     */
    @GetMapping("/by-department")
    public ApiResponse<List<Map<String, Object>>> getStatsByDepartment() {
        List<Map<String, Object>> result = statsService.getStatsByDepartment();
        return ApiResponse.success(result);
    }
    
    /**
     * 按员工统计
     */
    @GetMapping("/by-employee")
    public ApiResponse<List<Map<String, Object>>> getStatsByEmployee() {
        List<Map<String, Object>> result = statsService.getStatsByEmployee();
        return ApiResponse.success(result);
    }
}


