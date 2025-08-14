package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    @Query("""
            SELECT cs FROM ClassSession cs
            JOIN cs.baseSchedule schedule
            WHERE schedule.clazz.id = :clazzId
            ORDER BY cs.date ASC
            """)
    List<ClassSession> findByClazzId(Long clazzId);
}
