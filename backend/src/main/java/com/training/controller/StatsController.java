package com.training.controller;

import com.training.common.api.Result;
import com.training.dto.TongJiDTO;
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
    public Result<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = statsService.getOverviewStats();
        return Result.success(stats);
    }
    
    /**
     * 按培训项目统计
     */
    @GetMapping("/by-training")
    public Result<List<Map<String, Object>>> getStatsByTraining() {
        List<Map<String, Object>> result = statsService.getStatsByTraining();
        return Result.success(result);
    }
    
    /**
     * 按部门统计
     */
    @GetMapping("/by-department")
    public Result<List<Map<String, Object>>> getStatsByDepartment() {
        List<Map<String, Object>> result = statsService.getStatsByDepartment();
        return Result.success(result);
    }
    
    /**
     * 按员工统计
     */
    @GetMapping("/by-employee")
    public Result<List<Map<String, Object>>> getStatsByEmployee() {
        List<Map<String, Object>> result = statsService.getStatsByEmployee();
        return Result.success(result);
    }

    /**
     *统计全局性信息
     */
    @GetMapping("/tongji")
    public Result<TongJiDTO> getTongJi()
    {
        TongJiDTO toongji = statsService.getTongji();
        return Result.success(toongji);
    }

}


