package com.example.Attendance.management.controller.form;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AttendanceForm {


    private int id;


    private  int userId;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角英数字で入力してください")
    //@Size(max = 5, message = "アカウントは6文字以上20文字以内で入力してください")
    private String attendance;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "半角英数字で入力してください")
    private String leave;


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
        未申請, 申請中, 差戻済み〇, 差戻済みX, 承認済み
    }



}
