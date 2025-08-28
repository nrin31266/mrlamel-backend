package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.dto.LearnedSessionDto;
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

    @Query("""
            SELECT cs FROM ClassSession cs
            WHERE cs.date = :date
            ORDER BY cs.startTime ASC
            """)
    List<ClassSession> findTimeTableForAllByDay(LocalDate date);

    @Query("""
            SELECT cs FROM ClassSession cs
            WHERE (:beforeDate IS NULL OR cs.date >= :beforeDate)
              AND cs.date <= CURRENT_DATE
              AND (
                    (cs.date < CURRENT_DATE) 
                    OR (cs.date = CURRENT_DATE AND cs.endTime < CURRENT_TIME)
                  )
              AND cs.status = com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS.NOT_YET
            """)
    List<ClassSession> findMissedSessions(@Param("beforeDate") LocalDate beforeDate);

    @Query("""
               SELECT cs.id,
               a.status,
               ae.attendee.id,
               ae.attendee.fullName,
               ae.attendee.email
               FROM ClassSession cs
               JOIN cs.clazz c
               JOIN cs.attendances a
               JOIN a.attendanceEnrollment ae
               WHERE c.id = :clazzId
               AND cs.status = com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS.DONE
                AND a.status IN (com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.ABSENT,
                 com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.LATE,
                 com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.EXCUSED)
            """)
    List<Object[]> findAttendanceDetailsByClazzId(Long clazzId);

    @Query("""
            SELECT cs FROM ClassSession cs
            WHERE cs.clazz.id = :clazzId
              AND cs.status = com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS.DONE
            ORDER BY cs.date ASC, cs.startTime ASC
            """)
    List<ClassSession> findSessionLearnedByClassId(Long clazzId);


}
