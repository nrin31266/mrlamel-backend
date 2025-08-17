package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.CheckStudentDto;
import com.rin.mrlamel.feature.classroom.dto.req.*;
import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/classes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ClassController {
    ClassService classService;

    @PostMapping
    public ApiRes<Clazz> createClass(@RequestBody CreateClassRequest createClassRequest,
                                     Authentication authentication) {
        log.info("Creating a new class");
        Clazz createdClass = classService.createClass(createClassRequest, authentication);
        return ApiRes.success(createdClass);
    }
    @GetMapping
    public ApiRes<?> getAllClasses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String status
    ) {
        log.info("Fetching all classes with pagination and sorting");
        return ApiRes.success(classService.getAllClasses(page - 1, size, sortBy, sortDirection, status));
    }

    @GetMapping("/{classId}")
    public ApiRes<Clazz> getClassById(@PathVariable Long classId) {
        log.info("Fetching class with ID: {}", classId);
        Clazz clazz = classService.getClassById(classId);
        return ApiRes.success(clazz);
    }

    @PostMapping("/schedules")
    public ApiRes<ClassSchedule> createClassSchedule(@RequestBody CreateClassScheduleReq createClassScheduleReq) {
        log.info("Creating class schedule");
        return ApiRes.success(classService.createClassSchedule(createClassScheduleReq));
    }

    @PutMapping("/schedules/{classScheduleId}")
    public ApiRes<ClassSchedule> updateClassSchedule(
            @PathVariable Long classScheduleId,
            @RequestBody UpdateClassScheduleReq updateClassScheduleReq
    ) {
        log.info("Updating class schedule with ID: {}", classScheduleId);
        ClassSchedule updatedClassSchedule = classService.updateClassSchedule(classScheduleId, updateClassScheduleReq);
        return ApiRes.success(updatedClassSchedule);
    }

    @DeleteMapping("/schedules/{classScheduleId}")
    public ApiRes<Void> deleteClassSchedule(@PathVariable Long classScheduleId) {
        log.info("Deleting class schedule with ID: {}", classScheduleId);
        classService.deleteClassSchedule(classScheduleId);
        return ApiRes.success(null);
    }

    @PutMapping("/mark-ready/{clazzId}")
    public ApiRes<Clazz> markClassOnReady(
            @PathVariable Long clazzId,
            @RequestBody MarkClassOnReadyRq markClassOnReadyRq
    ) {
        log.info("Marking class with ID: {} as ready", clazzId);
        return ApiRes.success(classService.markClassOnReady(clazzId, markClassOnReadyRq));
    }

    @GetMapping("/{classId}/sessions")
    public ApiRes<List<ClassSession>> getClassSessionsByClassId(@PathVariable Long classId) {
        log.info("Fetching class sessions for class ID: {}", classId);
        List<ClassSession> classSessions = classService.getClassSessionsByClassId(classId);
        return ApiRes.success(classSessions);
    }

    @GetMapping("/{clazzId}/users/check")
    public ApiRes<CheckStudentDto> checkStudentBeforeAddingToClass(
            @RequestParam String studentEmail,
            @PathVariable Long clazzId
    ) {
        log.info("Checking student before adding to class with ID: {}", clazzId);
        return ApiRes.success(classService.checkStudentBeforeAddingToClass(studentEmail, clazzId));
    }

    @PostMapping("/{clazzId}/users")
    public ApiRes<ClassEnrollment> addStudentToClass(
            @RequestBody @Valid AddStudentToClassRq addStudentToClassRq,
            @PathVariable Long clazzId

    ) {
        log.info("Adding student to class with ID: {}", clazzId);
        return ApiRes.success(classService.addStudentToClass(addStudentToClassRq));
    }
    @GetMapping("/{classId}/enrollments")
    public ApiRes<List<ClassEnrollment>> getClassEnrollmentsByClassId(@PathVariable Long classId) {
        log.info("Fetching class enrollments for class ID: {}", classId);
        List<ClassEnrollment> enrollments = classService.getClassEnrollmentsByClassId(classId);
        return ApiRes.success(enrollments);
    }
    @DeleteMapping("/{classId}/users/{studentId}")
    public ApiRes<Void> removeStudentFromClass(@PathVariable Long classId, @PathVariable Long studentId) {
        log.info("Removing student with ID: {} from class with ID: {}", studentId, classId);
        classService.removeStudentFromClass(classId, studentId);
        return ApiRes.success(null);
    }
}
