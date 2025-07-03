package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.repository.AttendanceRepository;
import com.example.Attendance.management.repository.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    AttendanceRepository attendanceRepository;

    public void saveAttendance(AttendanceForm reqAttendance) throws ParseException {
        Attendance saveAttendance = setAttendanceEntity(reqAttendance);
        attendanceRepository.save(saveAttendance);
    }
    private Attendance setAttendanceEntity(AttendanceForm reqAttendance) throws ParseException {

        //指定の日付勤怠情報を取得できる
        String toDayA = reqAttendance.getDate() + " " + reqAttendance.getAttendance();
        String toDayL = reqAttendance.getDate()  + " " + reqAttendance.getLeave();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dtA = LocalDateTime.parse(toDayA, formatter);
        LocalDateTime dtL = LocalDateTime.parse(toDayL, formatter);

        Attendance attendance = new Attendance();
        attendance.setId(reqAttendance.getId());
        attendance.setComment(reqAttendance.getComment());
        attendance.setAttendance(dtA);
        attendance.setLeave(dtL);
        attendance.setStateId(0);
        attendance.setUserId(reqAttendance.getUserId());
        attendance.setCreatedDate(reqAttendance.getCreatedDate());
        attendance.setUpdatedDate(reqAttendance.getUpdatedDate());
        return attendance;
    }

        // 月間勤怠情報を取得
        public List<Attendance> getMonthlyAttendance(int userId, LocalDate month) {
            LocalDateTime startOfMonth = month.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = month.withDayOfMonth(month.lengthOfMonth())
                    .atTime(23, 59, 59);

            return attendanceRepository.findByUserIdAndAttendanceBetween(userId, startOfMonth, endOfMonth);
        }


}
