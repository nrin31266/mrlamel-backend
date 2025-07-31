package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.feature.classroom.model.Course;
import com.rin.mrlamel.feature.classroom.repository.CourseRepository;
import com.rin.mrlamel.feature.classroom.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    @Override
    public Course createCourse(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Course with code " + course.getCode() + " already exists.");
        }
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course with id " + id + " does not exist.");
        }
        course.setId(id);
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course with id " + id + " does not exist."));
    }

    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course with id " + id + " does not exist.");
        }
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
