package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.common.constant.ATTENDANCE_STATUS;
import com.rin.mrlamel.feature.classroom.dto.AttendanceDTO;
import com.rin.mrlamel.feature.classroom.dto.AttendanceSessionDTO;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.identity.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AttendanceService {
    // Define methods for attendance service here
    // For example:
    // List<Attendance> getAttendanceBySessionId(Long sessionId);
    // void markAttendance(Long sessionId, Long studentId, boolean present);

    AttendanceSessionDTO getAttendancesBySessionId(Long sessionId, Authentication authentication);
    AttendanceDTO markStatusAttendance(Long attendanceId, ATTENDANCE_STATUS status, Authentication authentication);

    // void updateAttendance(Long attendanceId, boolean present);
}
