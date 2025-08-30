package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.classroom.dto.ClassProgressDTO;
import com.rin.mrlamel.feature.classroom.dto.ClazzDto;
import com.rin.mrlamel.feature.classroom.dto.TimeTableByWeekDto;
import com.rin.mrlamel.feature.classroom.dto.TimeTableSessionDto;
import com.rin.mrlamel.feature.classroom.mapper.ClassMapper;
import com.rin.mrlamel.feature.classroom.service.AttendanceService;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student/classes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentClassController {
    ClassService classService;
    AttendanceService attendanceService;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;
    ClassMapper classMapper;

    @GetMapping("/time-table/day")
    public ApiRes<List<TimeTableSessionDto>> getTimeTableForStudentByDay(
            @RequestParam(value = "date", required = false) LocalDate date,
            Authentication authentication
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        Long studentId = (Long) jwtTokenProvider.getClaim(authentication, "id");
        return ApiRes.success(classService.getTimeTableForStudentByDay(studentId, date));
    }

    @GetMapping("/time-table/week")
    public ApiRes<TimeTableByWeekDto> getTimeTableForTeacherByWeek(
            Authentication authentication,
            @RequestParam(value = "weekNumber", required = false, defaultValue = "0") Integer weekNumber
    ) {
        log.info("Fetching timetable for week number: {}", weekNumber);
        Long studentId = (Long) jwtTokenProvider.getClaim(authentication, "id");
        TimeTableByWeekDto timeTable = classService.getTimeTableForStudentByWeek(studentId, weekNumber);
        return ApiRes.success(timeTable);
    }

    //    @GetMapping("/participated")
//    public ApiRes<List<ClazzDto>> findClassesByTeacherParticipated(
//            Authentication authentication
//    ) {
//        Long teacherId = (Long) jwtTokenProvider.getClaim(authentication, "id");
//        return ApiRes.success(classService.findTheClassesThatTheTeachersAreTeaching(teacherId));
//    }
    //getLearnedSessionsForClass
    @GetMapping("/class-progress/{classId}")
    public ApiRes<ClassProgressDTO> getLearnedSessionsForClass(@PathVariable Long classId) {
        return ApiRes.success(
                ClassProgressDTO.builder()
                        .learnedSessions(classService.getLearnedSessionsForClass(classId))
                        .clazz(classMapper.toClazzDTO(classService.getClassById(classId)))
                        .build());
    }
    // getClassesStudentIsEnrolledIn
    @GetMapping("/studying")
    public ApiRes<List<ClazzDto>> getClassesStudentStudying(
            Authentication authentication
    ) {
        Long studentId = (Long) jwtTokenProvider.getClaim(authentication, "id");

        return ApiRes.success( classService.getClassesStudentIsEnrolledIn(studentId,
                0,
                100,
                "id",
                "DESC",
                "ONGOING",
                ""));
    }



}
