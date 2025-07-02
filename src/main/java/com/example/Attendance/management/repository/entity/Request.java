package com.example.Attendance.management.repository.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "requests")
@Data

@Getter
@Setter
public class Request {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int userId;

    @Column
    private String state;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column(insertable = false, updatable = false)
    private Date createdDate;

    @Column(insertable = false)
    private Date updatedDate;
}
