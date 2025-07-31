package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.feature.classroom.model.Course;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
    Course getCourseById(Long id);
    void deleteCourse(Long id);
    List<Course> getAllCourses();


}
