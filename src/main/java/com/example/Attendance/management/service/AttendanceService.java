package com.example.Attendance.management.service;

import com.example.Attendance.management.repository.AttendanceRepository;
import com.example.Attendance.management.repository.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // 月間勤怠情報を取得
    public List<Attendance> getMonthlyAttendance(Long userId, LocalDate month) {
        LocalDate startOfMonth = LocalDate.from(month.withDayOfMonth(1).atStartOfDay());
        LocalDateTime endOfMonth = month.withDayOfMonth(month.lengthOfMonth()).atTime(23, 59, 59);

        return attendanceRepository.findByUserIdAndAttendanceBetween(userId, startOfMonth, LocalDate.from(endOfMonth));    }
}
