package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    @Query("""
            SELECT cs FROM ClassSession cs
            JOIN cs.baseSchedule schedule
            WHERE schedule.clazz.id = :clazzId
            ORDER BY cs.date ASC
            """)
    List<ClassSession> findByClazzId(Long clazzId);

    List<ClassSession> findByBaseScheduleId(Long classScheduleId);


    @Query("""
            SELECT cs FROM ClassSession cs
            WHERE cs.teacher.id = :teacherId
            AND cs.date = :date
            ORDER BY cs.startTime ASC
            """)
    List<ClassSession> findTimeTableForTeacherByDay(
            @Param("teacherId") Long teacherId,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT cs FROM ClassSession cs
            WHERE cs.teacher.id = :teacherId
            AND cs.date BETWEEN :startOfWeek AND :endOfWeek
            ORDER BY cs.date ASC, cs.startTime ASC
            """)
    List<ClassSession> findTimeTableForTeacherByWeek(
            Long teacherId,
            LocalDate startOfWeek,
            LocalDate endOfWeek
    );

    @Query("""
            SELECT cs FROM ClassSession cs
            JOIN cs.clazz c
            JOIN c.enrollments ce
            WHERE ce.attendee.id = :studentId
              AND cs.date = :date
            ORDER BY cs.startTime ASC
            """)
    List<ClassSession> findTimeTableForStudentByDay(
            @Param("studentId") Long studentId,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT cs FROM ClassSession cs
            JOIN cs.clazz c
            JOIN c.enrollments ce
            WHERE ce.attendee.id = :studentId
              AND cs.date BETWEEN :startOfWeek AND :endOfWeek
            ORDER BY cs.date ASC, cs.startTime ASC
            """)
    List<ClassSession> findTimeTableForStudentByWeek(
            @Param("studentId") Long studentId,
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek
    );

}
