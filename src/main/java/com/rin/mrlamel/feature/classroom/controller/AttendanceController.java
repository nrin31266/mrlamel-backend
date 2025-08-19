package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.constant.ATTENDANCE_STATUS;
import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.AttendanceDTO;
import com.rin.mrlamel.feature.classroom.dto.AttendanceSessionDTO;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.classroom.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendances")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AttendanceController {
    AttendanceService attendanceService;
    @GetMapping("/{sessionId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiRes<AttendanceSessionDTO> getAttendancesBySession(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        return ApiRes.success(attendanceService.getAttendancesBySessionId(sessionId,authentication));
    }
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/mark/{attendanceId}")
    public ApiRes<AttendanceDTO> markStatusAttendance(
            @PathVariable Long attendanceId,
            @RequestParam ATTENDANCE_STATUS status,
            Authentication authentication
    ) {
        return ApiRes.success(attendanceService.markStatusAttendance(attendanceId, status, authentication));
    }
}
