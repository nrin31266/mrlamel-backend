package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.dto.AbsenceCountDTO;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Các phương thức truy vấn tùy chỉnh có thể được định nghĩa ở đây
    // Ví dụ: tìm kiếm theo lớp học, theo ngày, v.v.


    List<Attendance> findBySessionId(Long sessionId);


    @Query("""
                SELECT new com.rin.mrlamel.feature.classroom.dto.AbsenceCountDTO(
                    a.attendanceEnrollment.id,
                    SUM(
                        CASE 
                            WHEN a.status = com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.ABSENT THEN CAST(1.0 as double)
                            WHEN a.status IN (com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.LATE, com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.EXCUSED) THEN CAST(0.5 as double)
                            ELSE CAST(0.0 as double)
                        END
                    )
                )
                FROM Attendance a
                LEFT JOIN a.session s
                WHERE s.date <= :date
                AND s.status = com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS.DONE
                AND a.attendanceEnrollment.id IN :enrollmentIds
                GROUP BY a.attendanceEnrollment.id
            """)
    List<AbsenceCountDTO> countAbsences(@Param("enrollmentIds") List<Long> enrollmentIds,
                                        @Param("date") LocalDate date);

    @Query("""
                SELECT new com.rin.mrlamel.feature.classroom.dto.AbsenceCountDTO(
                    a.attendanceEnrollment.id,
                    SUM(
                        CASE 
                            WHEN a.status = com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.ABSENT THEN CAST(1.0 as double)
                            WHEN a.status IN (com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.LATE, com.rin.mrlamel.common.constant.ATTENDANCE_STATUS.EXCUSED) THEN CAST(0.5 as double)
                            ELSE CAST(0.0 as double)
                        END
                    )
                )
                FROM Attendance a
                LEFT JOIN a.session s
                WHERE s.date <= :date
                AND s.status = com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS.DONE
                AND a.attendanceEnrollment.id = :enrollmentId
                GROUP BY a.attendanceEnrollment.id
            """)
    AbsenceCountDTO countAbsence(@Param("enrollmentId") Long enrollmentId,
                                        @Param("date") LocalDate date);

}
