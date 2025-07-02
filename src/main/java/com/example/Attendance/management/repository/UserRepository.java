package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByAccount(String account);
    List<User> findByAccountAndPassword(String account, String encPassword);
}
