package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByCode(String code);

    List<Room> findByBranchId(Long branchId);


//    @Query("""
//
//            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
//            FROM Room r
//            WHERE r.id = :roomId
//            AND NOT EXISTS (
//                SELECT cs FROM ClassSession cs
//                WHERE cs.room.id = r.id
//                AND cs.date = :dayOfWeek
//                AND (
//                    (cs.startTime < :endTime AND cs.endTime > :startTime)
//                )
//            )
//            """)
//    boolean isRoomAvailable(Long roomId);
}
