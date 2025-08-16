package com.rin.mrlamel.feature.identity.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teachers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TeacherController {
    UserService userService; // Uncomment and inject the service when implemented
    AuthenticationService authenticationService; // Uncomment and inject the service when implemented
    ClassService classService; // Uncomment and inject the service when implemented


    @GetMapping("/available")
    public ApiRes<List<User>> getAvailableTeachersForClasses(
            @RequestParam(required = false) Long clazzId,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(name = "mode") String mode
    ) {
        if(mode.equals("by-clazz") && clazzId != null) {
            return ApiRes.success(userService.getAvailableTeachersForSessions(classService.getClassSessionsByClassId(clazzId)));
        } else if(mode.equals("by-schedule") && scheduleId != null) {
            return ApiRes.success(userService.getAvailableTeachersForSessions(classService.getClassSessionsByClassScheduleId(scheduleId)));
        } else if(mode.equals("by-session") && sessionId != null) {
            return ApiRes.success(userService.getAvailableTeachersForSessions(List.of(classService.getClassSessionById(sessionId))));
        }
        return ApiRes.success(userService.getAllTeachers());
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("assignment")
    public ApiRes<Void> assignTeacher(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long clazzId,
            @RequestParam(name = "mode") String mode,
            @RequestParam Long teacherId
    ) {
        List<ClassSession> classSessions;
        if (mode.equals("by-clazz") && clazzId != null) {
            classSessions = classService.getClassSessionsByClassId(clazzId);
            Clazz clazz = classService.getClassById(clazzId);
            classService.assignTeacherToSchedules(teacherId, clazz.getSchedules());
        } else if (mode.equals("by-schedule") && scheduleId != null) {
            classSessions = classService.getClassSessionsByClassScheduleId(scheduleId);
            classService.assignTeacherToSchedules(teacherId, List.of(classService.getClassScheduleById(scheduleId)));
        } else if (mode.equals("by-session") && sessionId != null) {
            classSessions = List.of(classService.getClassSessionById(sessionId));
        } else {
            classSessions = new ArrayList<>();
        }
        userService.assignTeacherToSessions(teacherId, classSessions);
        return ApiRes.success(null);
    }

}
