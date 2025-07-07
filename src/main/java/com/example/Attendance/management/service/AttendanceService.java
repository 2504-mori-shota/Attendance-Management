package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.repository.AttendanceRepository;
import com.example.Attendance.management.repository.entity.Attendance;
import com.example.Attendance.management.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Attendance attendance = new Attendance();
        //申請用のsetEntity
        if(reqAttendance.getDate() == null){
            LocalDateTime dtA = LocalDateTime.parse(reqAttendance.getAttendance(), formatter);
            LocalDateTime dtL = LocalDateTime.parse(reqAttendance.getLeave(), formatter);
            attendance.setAttendance(dtA);
            attendance.setLeave(dtL);
            attendance.setId(reqAttendance.getId());
            attendance.setComment(reqAttendance.getComment());
            attendance.setUserId(reqAttendance.getUserId());
            attendance.setCreatedDate(reqAttendance.getCreatedDate());
            return attendance;
        }
        //指定の日付勤怠情報を取得できる
        String toDayA = reqAttendance.getDate() + " " + reqAttendance.getAttendance();
        String toDayL = reqAttendance.getDate()  + " " + reqAttendance.getLeave();


        LocalDateTime dtA = LocalDateTime.parse(toDayA, formatter);
        LocalDateTime dtL = LocalDateTime.parse(toDayL, formatter);
        attendance.setAttendance(dtA);
        attendance.setLeave(dtL);

        attendance.setId(reqAttendance.getId());
        attendance.setComment(reqAttendance.getComment());
        if (reqAttendance.getState() == null){
            attendance.setState(0);
        } else {
            attendance.setState(reqAttendance.getState());
        }
        attendance.setUserId(reqAttendance.getUserId());
        attendance.setCreatedDate(reqAttendance.getCreatedDate());
        attendance.setUpdatedDate(reqAttendance.getUpdatedDate());
        return attendance;
    }

    //月間の労働時間を合計するメソッド
    //Durationクラスは、時間の長さを表現するためのクラス。
    //LocalTimeを→Durationに。　LocalTimeは時間を加算できない。Durationにすることで複数の時間を合計することが可能に。
    public Duration calculateTotalWorkingTime(List<AttendanceForm> attendances) {
        //ストリームを使うことで、リストの各要素に対して順番に処理を行うことができる
        return attendances.stream()
                .map(AttendanceForm::getRestTime)
                //LocalTime 型の restTime を Duration 型に変換
                .map(time -> Duration.ofHours(time.getHour()).plusMinutes(time.getMinute()))
                //reduce はストリームの各要素を順番に処理し、最終的な結果を得るためのやつ
                .reduce(Duration.ZERO, Duration::plus);
    }

    // 月間勤怠情報を取得
    public List<AttendanceForm> getMonthlyAttendance(int userId, LocalDate month) {
        LocalDateTime startOfMonth = month.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = month.withDayOfMonth(month.lengthOfMonth())
                .atTime(23, 59, 59);
        List<Attendance> attendances = attendanceRepository.findByUserIdAndAttendanceBetweenOrderByAttendanceAsc(userId, startOfMonth, endOfMonth);
        List<AttendanceForm> attendanceForms = setAttendanceForm(attendances);
        return attendanceForms;
    }

    public List<AttendanceForm> findAttendanceByRequest(RequestForm requestForm){
        // Date → Localdatetime の変換
        LocalDateTime start = LocalDateTime.ofInstant(requestForm.getStartDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(requestForm.getEndDate().toInstant(), ZoneId.systemDefault());

        List<Attendance> attendances = attendanceRepository.findByUserIdAndAttendanceBetweenOrderByAttendanceAsc(requestForm.getUserId(),start,end);
        List<AttendanceForm> result = setAttendanceForm(attendances);
        return result;
    }

    private List<AttendanceForm> setAttendanceForm(List<Attendance> results) {
        List<AttendanceForm> attendances = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        for (int i = 0; i < results.size(); i++) {

            AttendanceForm attendance = new AttendanceForm();
            Attendance result = results.get(i);
            //出勤時間と退勤時間をLocalDateTime型 → String型に型変換
            String attendanceString = result.getAttendance().format(formatter);
            String leaveString = result.getLeave().format(formatter);
            //出勤時間を計算
            //between(出勤, 退勤)→正しく出る
            //between(退勤, 出勤）→計算がマイナスになる
            Duration diff = Duration.between(result.getAttendance(), result.getLeave());
            long hours = diff.toHours();
            long minutes = diff.toMinutes() % 60;
            //LocalTimeに型変換
            LocalTime restTime = LocalTime.of((int)hours, (int)minutes);

            attendance.setId(result.getId());
            attendance.setUserId(result.getUserId());
            attendance.setComment(result.getComment());
            attendance.setAttendance(attendanceString);
            attendance.setLeave(leaveString);
            attendance.setRestTime(restTime);
            attendance.setState(result.getState());
            attendance.setCreatedDate(result.getCreatedDate());
            attendance.setUpdatedDate(result.getUpdatedDate());
            attendances.add(attendance);
        }
        return attendances;
    }

    public AttendanceForm findById (int id) {
        List<Attendance> attendances = attendanceRepository.findById(id);
        List<AttendanceForm> attendanceForms = setAttendanceEditForm(attendances);
        return attendanceForms.get(0);
    }

    private List<AttendanceForm> setAttendanceEditForm(List<Attendance> results) {
        List<AttendanceForm> attendances = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


        for (int i = 0; i < results.size(); i++) {

            AttendanceForm attendance = new AttendanceForm();
            Attendance result = results.get(i);
            //出勤時間と退勤時間をLocalDateTime型 → String型に型変換
            String attendanceString = result.getAttendance().format(timeFormatter);
            String leaveString = result.getLeave().format(timeFormatter);
            //日付を取得
            String date = result.getAttendance().format(dateFormatter);


            attendance.setId(result.getId());
            attendance.setUserId(result.getUserId());
            attendance.setComment(result.getComment());
            attendance.setAttendance(attendanceString);
            attendance.setLeave(leaveString);
            attendance.setDate(date);
            attendance.setState(result.getState());
            attendance.setCreatedDate(result.getCreatedDate());
            attendance.setUpdatedDate(result.getUpdatedDate());
            attendances.add(attendance);
        }
        return attendances;
    }

    public void saveAttendanceState(List<AttendanceForm> attendanceForms) throws ParseException {
        for (int i = 0; i < attendanceForms.size(); i++) {

            AttendanceForm attendanceForm = attendanceForms.get(i);

            Attendance attendance = setAttendanceEntity(attendanceForm);
            attendance.setState(1);
            attendanceRepository.save(attendance);
        }



    }
}
