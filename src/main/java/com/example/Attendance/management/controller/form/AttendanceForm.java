package com.example.Attendance.management.controller.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
public class AttendanceForm {


    private int id;


    private  int userId;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角数字かつ23：59以内で入力してください")
    //@Size(max = 5, message = "アカウントは6文字以上20文字以内で入力してください")
    private String attendance;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角数字かつ23：59以内で入力してください")
    private String leave;

    @NotBlank(message = "休憩開始時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "休憩開始時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角数字かつ23：59以内で入力してください")
    private String restStart;

    @NotBlank(message = "休憩終了時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "休憩終了時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角数字かつ23：59以内で入力してください")
    private  String restEnd;


    @Pattern(regexp = "^[^ ]*$", message = "スペースのみは入力できません")
    @Pattern(regexp = "^[^　]*$", message = "スペースのみは入力できません")
    private String comment;


    private Integer state;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createdDate;

    private Date updatedDate;

    @NotBlank(message = "日付を指定してください")
    private String date;

    private LocalTime restTime;

    private Boolean checkbox;

    public enum Status {
        未申請, 申請中, 承認済み, 承認取消済,  差戻済み〇, 差戻済みX,
    }



}
