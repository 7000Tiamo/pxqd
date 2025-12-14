package com.training.mapper;

import com.training.entity.Training;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrainingMapper {
    Training selectById(Long id);

    List<Training> selectAll();

    List<Training> selectByCondition(@Param("keyword") String keyword, @Param("status") String status);

    int insert(Training training);

    int updateById(Training training);

    int deleteById(Long id);

    List<Training> selectByStatus(String status);
}
