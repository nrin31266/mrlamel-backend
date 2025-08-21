package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.classroom.dto.ClazzDto;
import com.rin.mrlamel.feature.classroom.dto.SessionDto;
import com.rin.mrlamel.feature.classroom.dto.TimeTableForTeacherByWeekDto;
import com.rin.mrlamel.feature.classroom.dto.TimeTableSessionDto;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.classroom.service.AttendanceService;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teacher/classes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('TEACHER')")
public class TeacherClassController {
    ClassService classService;
    AttendanceService attendanceService;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;
    @GetMapping("/{teacherId}/time-table/day")
    public ApiRes<List<TimeTableSessionDto>> getTimeTableForTeacherByDay(
            @PathVariable Long teacherId,
            @RequestParam(value = "date", required = false) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ApiRes.success(classService.getTimeTableForTeacherByDay(teacherId, date));
    }

    @GetMapping("/{teacherId}/time-table/week")
    public ApiRes<TimeTableForTeacherByWeekDto> getTimeTableForTeacherByWeek(
            @PathVariable Long teacherId,
            @RequestParam(value = "weekNumber", required = false, defaultValue = "0") Integer weekNumber
    ) {
        return ApiRes.success(classService.getTimeTableForTeacherByWeek(teacherId, weekNumber));
    }

    @GetMapping("/participated")
    public ApiRes<List<ClazzDto>> findClassesByTeacherParticipated(
            Authentication authentication
    ) {
        Long teacherId = (Long) jwtTokenProvider.getClaim(authentication, "id");
        return ApiRes.success(classService.findClassesByTeacherParticipated(teacherId));
    }

}
