package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {

    // Define custom query methods if needed
    // For example, to find enrollments by class ID or student ID

    boolean existsByClazzIdAndAttendeeId(Long clazzId, Long attendeeId);
    List<ClassEnrollment> findByClazzId(Long clazzId);
}
