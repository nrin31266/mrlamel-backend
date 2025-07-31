package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Additional query methods can be defined here if needed
    boolean existsByCode(String code);
}
