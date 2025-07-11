package com.example.Attendance.management.controller.form;

import com.example.Attendance.management.repository.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestForm {
    private int id;

    private int userId;

    private int state;

    private Date startDate;

    private Date endDate;

    private Date createdDate;

    private Date updatedDate;

    public enum Status {
        申請中, 差戻済み, 承認済み
    }

    private User user;

}
