package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByCode(String code);

    List<Room> findByBranchId(Long branchId);

    @Query("""
        SELECT r
        FROM Room r
        WHERE r.id NOT IN (
            SELECT s.room.id
            FROM ClassSession s
            WHERE s.date = :date
              AND s.room.id IS NOT NULL
              AND s.startTime < :endTime
              AND s.endTime > :startTime
              AND s.id <> :sessionId
              AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime >= CURRENT_TIME))
        )
    """)
    List<Room> getAvailableRoomForSession(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("sessionId") Long sessionId // session hiện tại để bỏ qua
    );

    /**
     * Đếm số session xung đột với Room chưa bắt đầu
     * Chỉ tính session chưa bắt đầu (ngày mai trở đi hoặc hôm nay nhưng chưa tới giờ bắt đầu)
     */
    @Query("""
        SELECT COUNT(s)
        FROM ClassSession s
        WHERE s.room.id = :roomId
          AND s.date = :date
          AND s.startTime < :endTime
          AND s.endTime > :startTime
          AND s.id <> :sessionId
          AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime >= CURRENT_TIME))
    """)
    long countConflictingSessions(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("sessionId") Long sessionId
    );
}
