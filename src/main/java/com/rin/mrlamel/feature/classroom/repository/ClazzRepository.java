package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ClazzRepository extends JpaRepository<Clazz, Long>, JpaSpecificationExecutor<Clazz> {

//    @Query("""
//        SELECT COUNT(s)
//        FROM ClassSession s
//        WHERE s.room.id = :roomId
//          AND s.date = :date
//          AND s.startTime < :endTime
//          AND s.endTime > :startTime
//          AND s.id <> :sessionId
//          AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime >= CURRENT_TIME))
//    """)
//    long countConflictingSessions(
//            @Param("roomId") Long roomId,
//            @Param("date") LocalDate date,
//            @Param("startTime") LocalTime startTime,
//            @Param("endTime") LocalTime endTime,
//            @Param("sessionId") Long sessionId
//    );

    @Query("""
    SELECT DISTINCT c
    FROM ClassSession s
    JOIN s.clazz c
    JOIN c.enrollments e
    WHERE e.attendee.id = :attendeeId
      AND c.id <> :clazzId
      AND (s.status = 'READY' OR s.status = 'ONGOING')
      AND s.date >= CURRENT_DATE
      AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.endTime > CURRENT_TIME))
""")
    List<Clazz> findClassesWithFutureSessionConflict(
            @Param("clazzId") Long clazzId,
            @Param("attendeeId") Long attendeeId
    );

//    SELECT DISTINCT c → tránh lặp class nếu có nhiều session trùng trong cùng một lớp.

}
