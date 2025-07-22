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

//    private Date restStart;
//
//    private Date restEnd;

    private Date createdDate;

    private Date updatedDate;

    public enum Status {
        未申請, 申請中, 承認済み, 承認取消済,  差戻済み,
    }

    private User user;

}
