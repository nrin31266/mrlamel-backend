package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.identity.service.ClassService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ClassController {
    ClassService classService;

    @PostMapping
    public ApiRes<Clazz> createClass(@RequestBody CreateClassRequest createClassRequest) {
        log.info("Creating a new class");
        Clazz createdClass = classService.createClass(createClassRequest);
        return ApiRes.success(createdClass);
    }
    @GetMapping
    public ApiRes<?> getAllClasses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String status
    ) {
        log.info("Fetching all classes with pagination and sorting");
        return ApiRes.success(classService.getAllClasses(page - 1, size, sortBy, sortDirection, status));
    }
}
