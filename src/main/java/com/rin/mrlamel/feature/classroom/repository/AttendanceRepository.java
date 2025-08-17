package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Các phương thức truy vấn tùy chỉnh có thể được định nghĩa ở đây
    // Ví dụ: tìm kiếm theo lớp học, theo ngày, v.v.


    List<Attendance> findBySessionId(Long sessionId);


}
