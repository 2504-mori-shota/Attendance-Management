package com.example.Attendance.management.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "users")
@Data

@Getter
@Setter
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account")
    private String account;

    @Column(name = "password", updatable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "is_stopped")
    private int isStopped;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Date createdDate;
    @Column(name="updated_date",insertable = false, updatable = false)
    private Date updatedDate;

    public enum isStopped {
        有効, 停止中
    }


    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

}
