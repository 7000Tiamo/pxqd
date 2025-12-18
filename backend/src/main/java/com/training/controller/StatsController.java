package com.training.controller;

import com.training.common.api.Result;
import com.training.dto.TongJiDTO;
import com.training.service.StatsService;
import com.training.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
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


    @GetMapping("/by-training/export")
    public void exportStatsByTraining(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = statsService.getStatsByTraining();

        // 可选：指定列顺序和中文名（推荐！避免 HashMap 乱序）
        Map<String, String> columnMapping = new LinkedHashMap<>();
        columnMapping.put("trainingId", "培训ID");
        columnMapping.put("training_name", "培训名称");
        columnMapping.put("appliedCount", "应到人数");
        columnMapping.put("signedCount", "签到人数");
        columnMapping.put("lateCount", "迟到");

        columnMapping.put("notSignedCount" ,"未签到人数");
        columnMapping.put("participationRate", "参与率");
        ExcelExportUtil.exportToExcel(
                data,
                "培训统计报表",      // sheet 名
                "培训参与统计",      // 文件名（会自动加 .xlsx）
                columnMapping,      // 列映射（可为 null）
                response
        );
    }
    /**
     * 按部门统计
     */
    @GetMapping("/by-department")
    public Result<List<Map<String, Object>>> getStatsByDepartment() {
        List<Map<String, Object>> result = statsService.getStatsByDepartment();
        return Result.success(result);
    }

    @GetMapping("/by-department/export")
    public void exportStatsByDepartment(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = statsService.getStatsByDepartment();

        // 可选：指定列顺序和中文名（推荐！避免 HashMap 乱序）
        Map<String, String> columnMapping = new LinkedHashMap<>();
        columnMapping.put("department", "部门");
        columnMapping.put("totalUsers", "总用户数");
        columnMapping.put("totalCheckins", "签到数");
        columnMapping.put("totalParticipation", "总参与数");
        columnMapping.put("participationRate", "参与率");
        ExcelExportUtil.exportToExcel(
                data,
                "培训统计报表",      // sheet 名
                "培训参与统计",      // 文件名（会自动加 .xlsx）
                columnMapping,      // 列映射（可为 null）
                response
        );
    }
    /**
     * 按员工统计
     */
    @GetMapping("/by-employee")
    public Result<List<Map<String, Object>>> getStatsByEmployee() {
        List<Map<String, Object>> result = statsService.getStatsByEmployee();
        return Result.success(result);
    }
    @GetMapping("/by-employee/export")
    public void exportStatsByEmployeet(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = statsService.getStatsByEmployee();
        Map<String, String> columnMapping = new LinkedHashMap<>();
        columnMapping.put("userName", "姓名");
        columnMapping.put("employeeNo", "工号");
        columnMapping.put("department", "部门");
        columnMapping.put("enrollments", "报名次数");
        columnMapping.put("checkins", "签到次数");
        columnMapping.put("lateCount", "迟到次数");

        // 3. 调用导出工具
        ExcelExportUtil.exportToExcel(
                data,
                "用户培训记录",      // sheet 名
                "用户统计",      // 文件名（会自动加 .xlsx）
                columnMapping,      // 列映射（可为 null）
                response
        );
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


