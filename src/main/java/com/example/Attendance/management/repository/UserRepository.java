package com.example.Attendance.management.repository;

import com.example.Attendance.management.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByAccount(String account);

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.post
            WHERE u.id = :id
            """)
    List<User> findByIdWithPost(@Param("id") int id);

    List<User> findById(int id);

    List<User> findAllByOrderByIdAsc();
}
