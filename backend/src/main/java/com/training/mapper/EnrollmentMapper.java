package com.training.mapper;

import com.training.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {
    Enrollment selectById(Long id);

    List<Enrollment> selectByTrainingId(Long trainingId);

    List<Enrollment> selectByUserId(Long userId);

    Enrollment selectByTrainingAndUser(@Param("trainingId") Long trainingId, @Param("userId") Long userId);

    int countByTrainingId(@Param("trainingId") Long trainingId);

    int insert(Enrollment enrollment);

    int updateById(Enrollment enrollment);

    int deleteById(Long id);
}
