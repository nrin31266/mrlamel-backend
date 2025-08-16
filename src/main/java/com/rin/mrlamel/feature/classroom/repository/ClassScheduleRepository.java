package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    @Query("""
    SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END
    FROM ClassSchedule cs
    WHERE cs.clazz.id = :classId
      AND cs.dayOfWeek = :dayOfWeek
      AND (cs.endTime > :startTime  AND cs.startTime < :endTime  )
      AND (cs.id <> :scheduleId OR :scheduleId IS NULL)
""")
    boolean existsByClazzIdAndDayOfWeekAndTimeOverlap(
            Long classId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Long scheduleId // Thêm tham số này để bỏ qua session hiện tại nếu cần thiết
    );




}
