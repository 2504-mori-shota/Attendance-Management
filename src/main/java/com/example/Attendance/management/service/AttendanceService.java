package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.repository.AttendanceRepository;
import com.example.Attendance.management.repository.entity.Attendance;

import com.example.Attendance.management.repository.entity.User;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
        Date date = new Date();
        Attendance attendance = new Attendance();
        //申請用のsetEntity
        if(reqAttendance.getDate() == null){
            LocalDateTime dtA = LocalDateTime.parse(reqAttendance.getAttendance(), formatter);
            LocalDateTime dtL = LocalDateTime.parse(reqAttendance.getLeave(), formatter);

            LocalDateTime dtRS = LocalDateTime.parse(reqAttendance.getRestStart(), formatter);
            LocalDateTime dtRL = LocalDateTime.parse(reqAttendance.getRestEnd(), formatter);

            attendance.setAttendance(dtA);
            attendance.setLeave(dtL);
            attendance.setId(reqAttendance.getId());
            attendance.setComment(reqAttendance.getComment());
            attendance.setUserId(reqAttendance.getUserId());
            attendance.setUpdatedDate(date);

            attendance.setRestStart(dtRS);
            attendance.setRestEnd(dtRL);
            return attendance;
        }
        //指定の日付勤怠情報を取得できる
        String todayAttendance = reqAttendance.getDate() + " " + reqAttendance.getAttendance();
        String todayLeave = reqAttendance.getDate()  + " " + reqAttendance.getLeave();

        String todayRestStart = reqAttendance.getDate() + " " + reqAttendance.getRestStart();
        String todayRestEnd = reqAttendance.getDate() + " " + reqAttendance.getRestEnd();

        LocalDateTime dtAttendance = LocalDateTime.parse(todayAttendance, formatter);
        LocalDateTime dtLeave = LocalDateTime.parse(todayLeave, formatter);

        LocalDateTime dtRestStrat = LocalDateTime.parse(todayRestStart,formatter);
        LocalDateTime dtRestEnd = LocalDateTime.parse(todayRestEnd,formatter);

        attendance.setAttendance(dtAttendance);
        attendance.setLeave(dtLeave);
        attendance.setRestStart(dtRestStrat);
        attendance.setRestEnd(dtRestEnd);

        attendance.setId(reqAttendance.getId());
        attendance.setComment(reqAttendance.getComment());
        if (reqAttendance.getState() == null){
            attendance.setState(0);
        } else {
            attendance.setState(reqAttendance.getState());
        }
        attendance.setUserId(reqAttendance.getUserId());
        attendance.setUpdatedDate(date);
        return attendance;
    }

    //月間の労働時間を合計するメソッド
    //Durationクラスは、時間の長さを表現するためのクラス。
    //LocalTimeを→Durationに。　LocalTimeは時間を加算できない。Durationにすることで複数の時間を合計することが可能に。
    public Duration TotalWorkingTime(List<AttendanceForm> attendances) {
        //ストリームを使うことで、リストの各要素に対して順番に処理を行うことができる
        return attendances.stream()
                .map(AttendanceForm::getWorkTime)
                //LocalTime 型の restTime を Duration 型に変換
                .map(time -> Duration.ofHours(time.getHour()).plusMinutes(time.getMinute()))
                //reduce はストリームの各要素を順番に処理し、最終的な結果を得るためのやつ
                .reduce(Duration.ZERO, Duration::plus);
    }
    public  Duration TotalRestTime(List<AttendanceForm> attendances) {
        return attendances.stream()
                .map(AttendanceForm ::getTotalRestTime)
                .map(mori -> Duration.ofHours(mori.getHour()).plusMinutes(mori.getMinute()))
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

    // 日付から勤怠情報取得
    public List<AttendanceForm> getDailyAttendance(int userId, LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(23, 59, 59);
        List<Attendance> attendances = attendanceRepository.findByUserIdAndAttendanceBetweenOrderByAttendanceAsc(userId, startOfDay, endOfDay);
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
            String restStartString = result.getRestStart().format(formatter);
            String restEndString = result.getRestEnd().format(formatter);

            //出勤時間を計算
            //between(出勤, 退勤)→正しく出る
            //between(退勤, 出勤）→計算がマイナスになる
            Duration diff = Duration.between(result.getAttendance(), result.getLeave());
            long hours = diff.toHours();
            long minutes = diff.toMinutes() % 60;
            //LocalTimeに型変換
            LocalTime workTime = LocalTime.of((int)hours, (int)minutes);

            Duration restDiff = Duration.between(result.getRestStart(),result.getRestEnd());
            long restHours = restDiff.toHoursPart();
            long restMinutes = restDiff.toMinutesPart();
            LocalTime totalRestTime = LocalTime.of((int)restHours,(int)restMinutes);

            Duration workRestDiff = Duration.between(totalRestTime,workTime);
            long workRestHours = workRestDiff.toHoursPart();
            long workRestMinutes = workRestDiff.toMinutesPart();
            LocalTime totalWorkRestTime = LocalTime.of((int)workRestHours,(int)workRestMinutes);


            attendance.setId(result.getId());
            attendance.setUserId(result.getUserId());
            attendance.setComment(result.getComment());
            attendance.setAttendance(attendanceString);
            attendance.setLeave(leaveString);
            attendance.setWorkTime(workTime);
            attendance.setTotalRestTime(totalRestTime);

            attendance.setRestStart(restStartString);
            attendance.setRestEnd(restEndString);
            attendance.setTotalWorkRestTime(totalWorkRestTime);

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
        if (attendanceForms.isEmpty()) {
            return null;
        }
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
            //休憩開始時間、終了時間をLocalDateTime型 → String型に型変換
            String restStartString = result.getRestStart().format(timeFormatter);
            String restEndString = result.getRestEnd().format(timeFormatter);

            attendance.setId(result.getId());
            attendance.setUserId(result.getUserId());
            attendance.setComment(result.getComment());
            attendance.setAttendance(attendanceString);
            attendance.setLeave(leaveString);

            attendance.setRestStart(restStartString);
            attendance.setRestEnd(restEndString);

            attendance.setDate(date);
            attendance.setState(result.getState());
            attendance.setCreatedDate(result.getCreatedDate());
            attendance.setUpdatedDate(result.getUpdatedDate());
            attendances.add(attendance);
        }
        return attendances;
    }

    public void saveAttendanceState(List<AttendanceForm> attendanceForms,int state) throws ParseException {
        for (int i = 0; i < attendanceForms.size(); i++) {

            AttendanceForm attendanceForm = attendanceForms.get(i);

            Attendance attendance = setAttendanceEntity(attendanceForm);
            attendance.setState(state);
            attendanceRepository.save(attendance);
        }

    }
    @Transactional
    public void deleteAttendance(Integer id){
        attendanceRepository.deleteById(id.longValue());
        }


    //同日の情報取得用
    public List<AttendanceForm> findAllByUserId (int id, String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate monthDay = LocalDate.parse(date, dateFormatter);
        LocalDateTime start = monthDay.atStartOfDay();
        LocalDateTime end = monthDay.plusDays(1).atStartOfDay();
        List<Attendance> attendances = attendanceRepository.findByUserIdAndAttendanceBetween(id, start, end);
        List<AttendanceForm> attendanceForms = setAttendanceEditForm(attendances);
        return attendanceForms;
    }

    //前・登録済み情報　後・登録前情報
    public boolean findByTime(AttendanceForm attendanceList, AttendanceForm attendanceForm){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //登録済み(already)の時間をLocalDateTime型に変換
        String aStartTime = attendanceList.getDate() + " " + attendanceList.getAttendance();
        String aEndTime = attendanceList.getDate() + " " + attendanceList.getLeave();
        LocalDateTime aStart = LocalDateTime.parse(aStartTime, dateFormatter);
        LocalDateTime aEnd = LocalDateTime.parse(aEndTime, dateFormatter);

        //登録前(still)の勤怠時間をLocalDateTime型に変換
        String sStartTime = attendanceForm.getDate() + " " + attendanceForm.getAttendance();
        String sEndTime = attendanceForm.getDate() + " " + attendanceForm.getLeave();
        LocalDateTime sStart = LocalDateTime.parse(sStartTime, dateFormatter);
        LocalDateTime sEnd = LocalDateTime.parse(sEndTime, dateFormatter);

        if (attendanceList.getId() != attendanceForm.getId()) {
            //isAfter = ～以降　　isBefore　= ～以前
            //sStart.isAfter(aStart)　は　登録前が登録済みよりも後の時間かどうか
            if (sStart.isAfter(aStart) && sStart.isBefore(aEnd)) {
                return true;
            }
            if (sEnd.isAfter(aStart) && sEnd.isBefore(aEnd)) {
                return true;
            }
            if (aStart.isAfter(sStart) && aStart.isBefore(sEnd)) {
                return true;
            }
            if (aEnd.isAfter(sStart) && aEnd.isBefore(sEnd)) {
                return true;
            }
            //出勤時間と退勤時間が両方とも被っていた時用の処理
            if (sStart.isEqual(aStart)) {
                return true;
            }
        }
        return false;
    }

}
