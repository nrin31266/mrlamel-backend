package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.Clazz;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            SELECT DISTINCT c2
            FROM ClassSession s2
            JOIN s2.clazz c2
            JOIN c2.enrollments e2
            WHERE e2.attendee.id = :attendeeId
              AND c2.id <> :clazzId
              AND c2.status IN (com.rin.mrlamel.common.constant.CLASS_STATUS.READY,
              com.rin.mrlamel.common.constant.CLASS_STATUS.ONGOING)
              AND EXISTS (
                SELECT 1
                FROM ClassSession s1
                WHERE s1.clazz.id = :clazzId
                  AND s1.date = s2.date
                  AND s1.startTime < s2.endTime
                  AND s1.endTime > s2.startTime
                  AND (s1.date > CURRENT_DATE OR (s1.date = CURRENT_DATE AND s1.endTime >= CURRENT_TIME))
              )
            """)
    List<Clazz> findClassesWithAnyFutureOverlapAgainstClazz(
            @Param("clazzId") Long clazzId,
            @Param("attendeeId") Long attendeeId
    );



    @Query("""
        SELECT c 
        FROM Clazz c
        WHERE c.status = 'READY'
          AND c.startDate <= :date
          AND (c.endDate IS NULL OR c.endDate >= :date)
    """)
    List<Clazz> findClassesReadyToStart(@Param("date") LocalDate date);


//    Nếu endDate IS NULL → mình lấy MAX(s.date) (ngày cuối của session).
//
//    Nếu MAX(s.date) < :dateMinusOne nghĩa là hiện tại đã qua luôn cả last session + 1 ngày → class coi như kết thúc.
    @Query("""
    SELECT c
    FROM Clazz c
    WHERE (c.status = 'ONGOING' OR c.status = 'READY')
      AND (
        (c.endDate IS NOT NULL AND c.endDate < :date)
        OR
        (
          c.endDate IS NULL
          AND EXISTS (
            SELECT 1
            FROM ClassSession s
            WHERE s.clazz = c
            GROUP BY s.clazz
            HAVING MAX(s.date) < :dateMinusOne
          )
        )
      )
""")
    List<Clazz> findAllOngoingClassesEndedBefore(
            @Param("date") LocalDate date,
            @Param("dateMinusOne") LocalDate dateMinusOne
    );


    @Query("""
               SELECT c
                FROM Clazz c
                WHERE c.status = com.rin.mrlamel.common.constant.CLASS_STATUS.ONGOING
                  AND EXISTS (
                    SELECT 1
                    FROM ClassSession s
                    WHERE s.clazz = c
                        AND s.teacher.id = :teacherId
                  )
            """)
    List<Clazz> getClassesTeacherIsTeaching(
            @Param("teacherId") Long teacherId
    );



}
