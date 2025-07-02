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

import java.util.Date;

@Getter
@Setter
public class AttendanceForm {


    private int id;


    private  int userId;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
    //@Size(max = 5, message = "アカウントは6文字以上20文字以内で入力してください")
    private String attendance;

    @NotBlank(message = "出勤時間を入力してください")
    @Pattern(regexp = "^[^　]*$", message = "出勤時間を入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
    private String leave;


    private String comment;


    private String state;


    private Date createdDate;

    private Date updatedDate;

}
