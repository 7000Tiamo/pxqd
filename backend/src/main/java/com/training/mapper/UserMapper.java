package com.training.mapper;

import com.training.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(Long id);

    List<User> selectAll();

    List<User> selectByCondition(@Param("keyword") String keyword);

    int insert(User user);

    int updateById(User user);

    int deleteById(Long id);

    User selectByUsername(String username);

    User selectByEmployeeNo(String employeeNo);

    User selectByUsernameAndEmployeeNo(@Param("username") String username, @Param("employeeNo") String employeeNo);

    int countByEmployeeNo(@Param("employeeNo") String employeeNo, @Param("excludeId") Long excludeId);

    int selectByStatus();
}
