package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.Request;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository {
    List<Request> findOderByCreatedDate();
}
