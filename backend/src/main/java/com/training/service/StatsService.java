package com.training.service;

import com.training.entity.Checkin;
import com.training.entity.Enrollment;
import com.training.entity.Training;
import com.training.entity.User;
import com.training.mapper.CheckinMapper;
import com.training.mapper.EnrollmentMapper;
import com.training.mapper.TrainingMapper;
import com.training.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

        private final TrainingMapper trainingMapper;
        private final EnrollmentMapper enrollmentMapper;
        private final CheckinMapper checkinMapper;
        private final UserMapper userMapper;

        /**
         * 获取首页统计数据
         */
        public Map<String, Object> getOverviewStats() {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

                List<Training> allTrainings = trainingMapper.selectAll();
                long monthTrainings = allTrainings.stream()
                                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(monthStart))
                                .count();

                List<Enrollment> allEnrollments = new ArrayList<>();
                for (Training t : allTrainings) {
                        allEnrollments.addAll(enrollmentMapper.selectByTrainingId(t.getId()));
                }
                long currentEnrollments = allEnrollments.stream()
                                .filter(e -> "enrolled".equals(e.getStatus()))
                                .count();

                List<Checkin> allCheckins = new ArrayList<>();
                for (Training t : allTrainings) {
                        allCheckins.addAll(checkinMapper.selectByTrainingId(t.getId()));
                }
                long monthCheckins = allCheckins.stream()
                                .filter(c -> c.getCheckinTime() != null && c.getCheckinTime().isAfter(monthStart))
                                .count();

                long completedCount = allCheckins.stream()
                                .filter(c -> "checked_out".equals(c.getState()))
                                .count();

                double completionRate = monthCheckins > 0 ? (completedCount * 100.0 / monthCheckins) : 0.0;

                Map<String, Object> stats = new HashMap<>();
                stats.put("monthTrainings", monthTrainings);
                stats.put("currentEnrollments", currentEnrollments);
                stats.put("monthCheckins", monthCheckins);
                stats.put("completedCount", completedCount);
                stats.put("completionRate", String.format("%.1f", completionRate) + "%");

                return stats;
        }

        /**
         * 按培训项目统计
         */
        public List<Map<String, Object>> getStatsByTraining() {
                List<Training> trainings = trainingMapper.selectAll();
                List<Map<String, Object>> result = new ArrayList<>();

                for (Training training : trainings) {
                        if (training.getDeleted() == 1)
                                continue;

                        Map<String, Object> item = new HashMap<>();
                        item.put("trainingId", training.getId());
                        item.put("trainingName", training.getTitle());

                        int appliedCount = enrollmentMapper.countByTrainingId(training.getId());
                        List<Checkin> checkins = checkinMapper.selectByTrainingId(training.getId());

                        long signedCount = checkins.stream()
                                        .filter(c -> "signed".equals(c.getState())
                                                        || "checked_out".equals(c.getState()))
                                        .count();
                        long lateCount = checkins.stream()
                                        .filter(c -> c.getIsLate() != null && c.getIsLate())
                                        .count();
                        long notSignedCount = appliedCount - signedCount;
                        double participationRate = appliedCount > 0 ? (signedCount * 100.0 / appliedCount) : 0.0;

                        item.put("appliedCount", appliedCount);
                        item.put("signedCount", signedCount);
                        item.put("lateCount", lateCount);
                        item.put("notSignedCount", notSignedCount);
                        item.put("participationRate", String.format("%.1f", participationRate) + "%");

                        result.add(item);
                }

                return result;
        }

        /**
         * 按部门统计
         */
        public List<Map<String, Object>> getStatsByDepartment() {
                List<User> users = userMapper.selectAll();
                Map<String, List<User>> deptMap = users.stream()
                                .filter(u -> u.getDept() != null && u.getDeleted() == 0)
                                .collect(Collectors.groupingBy(User::getDept));

                List<Map<String, Object>> result = new ArrayList<>();

                for (Map.Entry<String, List<User>> entry : deptMap.entrySet()) {
                        String dept = entry.getKey();
                        List<User> deptUsers = entry.getValue();

                        Map<String, Object> item = new HashMap<>();
                        item.put("department", dept);
                        int totalUsers = deptUsers.size();

                        long totalParticipations = 0;
                        long totalCheckins = 0;

                        for (User user : deptUsers) {
                                List<Enrollment> enrollments = enrollmentMapper.selectByUserId(user.getId());
                                totalParticipations += enrollments.stream()
                                                .filter(e -> "enrolled".equals(e.getStatus()))
                                                .count();

                                List<Checkin> checkins = checkinMapper.selectByUserId(user.getId());
                                totalCheckins += checkins.size();
                        }

                        double participationRate = totalUsers > 0 ? (totalCheckins * 100.0 / totalUsers) : 0.0;

                        item.put("totalUsers", totalUsers);
                        item.put("totalParticipations", totalParticipations);
                        item.put("totalCheckins", totalCheckins);
                        item.put("participationRate", String.format("%.1f", participationRate) + "%");

                        result.add(item);
                }

                return result;
        }

        /**
         * 按员工统计
         */
        public List<Map<String, Object>> getStatsByEmployee() {
                List<User> users = userMapper.selectAll();
                List<Map<String, Object>> result = new ArrayList<>();

                for (User user : users) {
                        if (user.getDeleted() == 1)
                                continue;

                        Map<String, Object> item = new HashMap<>();
                        item.put("userId", user.getId());
                        item.put("userName", user.getName());
                        item.put("employeeNo", user.getEmployeeNo());
                        item.put("department", user.getDept());

                        List<Enrollment> enrollments = enrollmentMapper.selectByUserId(user.getId());
                        long enrollCount = enrollments.stream()
                                        .filter(e -> "enrolled".equals(e.getStatus()))
                                        .count();

                        List<Checkin> checkins = checkinMapper.selectByUserId(user.getId());
                        long checkinCount = checkins.stream()
                                        .filter(c -> "signed".equals(c.getState())
                                                        || "checked_out".equals(c.getState()))
                                        .count();
                        long lateCount = checkins.stream()
                                        .filter(c -> c.getIsLate() != null && c.getIsLate())
                                        .count();

                        item.put("enrollments", enrollCount);
                        item.put("checkins", checkinCount);
                        item.put("lateCount", lateCount);

                        result.add(item);
                }

                return result;
        }
}
