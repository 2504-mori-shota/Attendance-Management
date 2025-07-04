package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 指定したユーザーの月間勤怠情報を取得
    List<Attendance> findByUserIdAndAttendanceBetween(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
