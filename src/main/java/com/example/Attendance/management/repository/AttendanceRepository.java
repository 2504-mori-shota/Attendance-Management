package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 指定したユーザーの月間勤怠情報を取得
    List<Attendance> findByUserIdAndAttendanceBetweenOrderByAttendanceAsc(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Attendance> findById(int id);
    //"yyyy-MM"で勤怠情報取得
    @Query("SELECT a FROM Attendance a WHERE a.userId = :userId AND a.attendance >= :start AND a.attendance <= :end")
    List<Attendance> findByUserIdAndAttendanceBetween(@Param("userId")int userId, @Param("start")LocalDateTime start, @Param("end") LocalDateTime end);
    //"HH:mm"で勤怠時間取得
    @Query("SELECT a FROM Attendance a WHERE a.userId = :userId AND a.attendance > :start AND a.leave < :end")
    List<Attendance> findByUserIdAndAttendanceANDLeaveBetween(@Param("userId")int userId, @Param("start")LocalDateTime start, @Param("end") LocalDateTime end);

}
