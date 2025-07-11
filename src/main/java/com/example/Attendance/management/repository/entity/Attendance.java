package com.example.Attendance.management.repository.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "attendances")
@Data

@Getter
@Setter
public class Attendance {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private  int userId;

    @Column
    private LocalDateTime attendance;

    @Column
    private LocalDateTime leave;

    @Column
    private String comment;

    @Column
    private int state;

    @Column(insertable = false, updatable = false)
    private Date createdDate;
    @Column(insertable = false)
    private Date updatedDate;

}
