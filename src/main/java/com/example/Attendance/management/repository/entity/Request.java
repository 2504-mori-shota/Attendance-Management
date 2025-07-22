package com.example.Attendance.management.repository.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private int state;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column(insertable = false, updatable = false, name="created_date")
    private Date createdDate;

    @Column(insertable = false, name="updated_date")
    private Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;
}
