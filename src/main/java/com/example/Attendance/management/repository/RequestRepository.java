package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.Attendance;
import com.example.Attendance.management.repository.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByOrderByCreatedDate();

    List<Request> findById(int id);

}
