package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.model.Course;
import com.rin.mrlamel.feature.classroom.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseController {
     CourseService courseService; // Uncomment and inject the service when implemented

    // Define endpoints for course management here
    // Example: @GetMapping, @PostMapping, etc.

    // Add methods to handle requests, e.g., createCourse, getCourse, updateCourse, deleteCourse
    @PostMapping
    public ApiRes<?> createCourse(@RequestBody Course request) {
        // Implement the logic to create a course
        // For now, return a placeholder response
        return ApiRes.success(courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    public ApiRes<?> updateCourse(@PathVariable Long id, @RequestBody Course request) {
        // Implement the logic to update a course
        return ApiRes.success(courseService.updateCourse(id, request));
    }

    @GetMapping("/{id}")
    public ApiRes<?> getCourseById(@PathVariable Long id) {
        // Implement the logic to get a course by ID
        return ApiRes.success(courseService.getCourseById(id));
    }
    @DeleteMapping("/{id}")
    public ApiRes<?> deleteCourse(@PathVariable Long id) {
        // Implement the logic to delete a course
        courseService.deleteCourse(id);
        return ApiRes.success("Course deleted successfully");
    }
    @GetMapping
    public ApiRes<?> getAllCourses() {
        // Implement the logic to get all courses
        return ApiRes.success(courseService.getAllCourses());
    }
}
