package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByOrderByCreatedDate();

    List<Request> findById(int id);
    List<Request> findByUserId(int userId);
}
