package com.example.Attendance.management.repository.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDate attendance;

    @Column
    private LocalDate leave;

    @Column
    private String comment;

    @Column(nullable = false)
    private short state = 0;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();
}
