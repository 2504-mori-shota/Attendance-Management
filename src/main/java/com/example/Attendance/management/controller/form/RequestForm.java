package com.example.Attendance.management.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestForm {
    private int id;

    private int userId;

    private String state;

    private Date startDate;

    private Date endDate;

    private Date createdDate;

    private Date updatedDate;

}
