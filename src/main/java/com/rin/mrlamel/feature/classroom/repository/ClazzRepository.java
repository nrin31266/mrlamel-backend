package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.model.Room;
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
      AND (c.status = 'READY' OR c.status = 'ONGOING')
      AND s.date >= CURRENT_DATE
      AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.endTime >= CURRENT_TIME))
""")
    List<Clazz> findClassesWithFutureSessionConflict(
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
                WHERE c.status IN :statuses
                  AND EXISTS (
                    SELECT 1
                    FROM ClassSession s
                    WHERE s.clazz = c
                        AND s.teacher.id = :teacherId
                  )
            """)
    List<Clazz> findClassesByTeacherParticipatedByStatuses(
            @Param("teacherId") Long teacherId,
            @Param("statuses") List<CLASS_STATUS> statuses
    );





}
