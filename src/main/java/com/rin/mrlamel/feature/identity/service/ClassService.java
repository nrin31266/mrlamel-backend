package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClassService {
    Clazz createClass(CreateClassRequest createClassReq, Authentication authentication);
    PageableDto<Clazz> getAllClasses(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String status
    );
    Clazz getClassById(Long classId);
}
