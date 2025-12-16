package com.training.controller;

import com.training.common.PageResult;
import com.training.common.api.Result;
import com.training.dto.TrainingCreateDTO;
import com.training.dto.TrainingUpdateDTO;
import com.training.entity.Training;
import com.training.service.TrainingService;
import com.training.vo.TrainingDetailVO;
import com.training.vo.TrainingListVO;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    /**
     * 创建培训
     */
    @PostMapping
    public Result<Training> createTraining(@Valid @RequestBody TrainingCreateDTO dto) {
        Training training = trainingService.createTraining(dto);
        return Result.success(training);
    }

    /**
     * 更新培训
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateTraining(@PathVariable Long id, @RequestBody TrainingUpdateDTO dto) {
        boolean result = trainingService.updateTraining(id, dto);
        return result ? Result.success(true) : Result.error("培训不存在");
    }

    /**
     * 发布培训
     */
    @PostMapping("/{id}/publish")
    public Result<Boolean> publishTraining(@PathVariable Long id) {
        try {
            boolean result = trainingService.publishTraining(id);
            return result ? Result.success(true) : Result.error("培训不存在");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询培训列表
     */
    @GetMapping
    public Result<PageResult<TrainingListVO>> getTrainingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        PageResult<TrainingListVO> result = trainingService.getTrainingPage(page, size, keyword, status);
        return Result.success(result);
    }

    /**
     * 获取培训详情
     */
    @GetMapping("/{id}")
    public Result<TrainingDetailVO> getTrainingDetail(@PathVariable Long id) {
        TrainingDetailVO vo = trainingService.getTrainingDetail(id);
        return vo != null ? Result.success(vo) : Result.error("培训不存在");
    }

    /**
     * 公开获取培训基本信息（用于扫码签到/签退页面）
     */
    @GetMapping("/public/{id}")
    public Result<Training> getPublicTrainingInfo(@PathVariable Long id) {
        Training training = trainingService.getById(id);
        if (training == null) {
            return Result.error("培训不存在");
        }
        // 只返回基本信息，不包含统计信息
        return Result.success(training);
    }

    /**
     * 删除培训（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTraining(@PathVariable Long id) {
        try {
            boolean result = trainingService.deleteTraining(id);
            return result ? Result.success(true) : Result.error("培训不存在");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
