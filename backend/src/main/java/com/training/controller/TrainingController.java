package com.training.controller;

import com.training.common.PageResult;
import com.training.common.api.ApiResponse;
import com.training.dto.TrainingCreateDTO;
import com.training.dto.TrainingDetailVO;
import com.training.dto.TrainingUpdateDTO;
import com.training.entity.Training;
import com.training.service.TrainingService;
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
    public ApiResponse<Training> createTraining(@Valid @RequestBody TrainingCreateDTO dto) {
        Training training = trainingService.createTraining(dto);
        return ApiResponse.success(training);
    }

    /**
     * 更新培训
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateTraining(@PathVariable Long id, @RequestBody TrainingUpdateDTO dto) {
        boolean result = trainingService.updateTraining(id, dto);
        return result ? ApiResponse.success(true) : ApiResponse.error("培训不存在");
    }

    /**
     * 发布培训
     */
    @PostMapping("/{id}/publish")
    public ApiResponse<Boolean> publishTraining(@PathVariable Long id) {
        try {
            boolean result = trainingService.publishTraining(id);
            return result ? ApiResponse.success(true) : ApiResponse.error("培训不存在");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分页查询培训列表
     */
    @GetMapping
    public ApiResponse<PageResult<Training>> getTrainingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        PageResult<Training> result = trainingService.getTrainingPage(page, size, keyword, status);
        return ApiResponse.success(result);
    }

    /**
     * 获取培训详情
     */
    @GetMapping("/{id}")
    public ApiResponse<TrainingDetailVO> getTrainingDetail(@PathVariable Long id) {
        TrainingDetailVO vo = trainingService.getTrainingDetail(id);
        return vo != null ? ApiResponse.success(vo) : ApiResponse.error("培训不存在");
    }

    /**
     * 删除培训（软删除）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteTraining(@PathVariable Long id) {
        try {
            boolean result = trainingService.deleteTraining(id);
            return result ? ApiResponse.success(true) : ApiResponse.error("培训不存在");
        } catch (Exception e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
}
