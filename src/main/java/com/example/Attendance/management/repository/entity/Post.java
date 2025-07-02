package com.example.Attendance.management.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "posts")
@Data

@Getter
@Setter
public class Post {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String postName;

    @Column(insertable = false, updatable = false)
    private Date createdDate;
    @Column(insertable = false)
    private Date updatedDate;
}
