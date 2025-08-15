package com.rin.mrlamel.feature.identity.repository;

import com.rin.mrlamel.feature.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
    SELECT COUNT(s)
    FROM ClassSession s
    WHERE s.teacher.id = :teacherId
      AND s.date = :date
      AND s.startTime < :endTime
      AND s.endTime > :startTime
      AND s.id <> :currentSessionId
      AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime >= CURRENT_TIME))
""")
    long countConflictingSessions(
            @Param("teacherId") Long teacherId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("currentSessionId") Long currentSessionId // dùng 0 hoặc session hiện tại để bỏ qua
    );

    @Query("""
    SELECT u
    FROM User u
    WHERE u.role = 'TEACHER'
      AND u.status = 'OK'
      AND u.id NOT IN (
          SELECT s.teacher.id
          FROM ClassSession s
          WHERE s.date = :date
            AND s.startTime < :endTime
            AND s.endTime > :startTime
            AND s.id <> :currentSessionId
            AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime >= CURRENT_TIME))
      )
""")
    List<User> getAvailableTeachersForSession(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("currentSessionId") Long currentSessionId
    );


    @Query("""
        SELECT u
        FROM User u
        WHERE u.role = 'TEACHER'
        AND u.status = 'OK'
    """)
    List<User> findAllTeachers();
}
