package com.training.mapper;

import com.training.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckinMapper {
    Checkin selectById(Long id);

    List<Checkin> selectByTrainingId(Long trainingId);

    List<Checkin> selectByUserId(Long userId);

    Checkin selectByTrainingAndUser(@Param("trainingId") Long trainingId, @Param("userId") Long userId);

    int insert(Checkin checkin);

    int updateById(Checkin checkin);

    int deleteById(Long id);
}
