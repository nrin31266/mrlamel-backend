package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    @Query("""
    SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END
    FROM ClassSchedule cs
    WHERE cs.clazz.id = :classId
      AND cs.dayOfWeek = :dayOfWeek
      AND (cs.endTime > :startTime  AND cs.startTime < :endTime  )
""")
    boolean existsByClazzIdAndDayOfWeekAndTimeOverlap(
            Long classId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );


}
