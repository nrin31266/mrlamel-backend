package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.mapper.ClassMapper;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.model.Course;
import com.rin.mrlamel.feature.classroom.repository.ClazzRepository;
import com.rin.mrlamel.feature.classroom.repository.CourseRepository;
import com.rin.mrlamel.feature.classroom.repository.RoomRepository;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.ClassService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ClassServiceImpl implements ClassService {
    ClazzRepository clazzRepository;
    ClassMapper classMapper;
    PageableMapper pageableMapper;
    RoomRepository roomRepository;
    CourseRepository courseRepository;

    @Override
    public Clazz createClass(CreateClassRequest createClassReq) {
        Clazz clazz = classMapper.toClass(createClassReq);
        clazz.setCourse(
                courseRepository.findById(createClassReq.getCourseId())
                        .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + createClassReq.getCourseId()))
        );
        clazz.setRoom(
                roomRepository.findById(createClassReq.getRoomId())
                        .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + createClassReq.getRoomId()))
        );
        clazz.setStatus(CLASS_STATUS.DRAFT); // Default status
        return clazzRepository.save(clazz);
    }

    @Override
    public PageableDto<Clazz> getAllClasses(int page, int size, String sortBy, String sortDirection, String status) {
        Specification<Clazz> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(page , size, sort);
        Page<Clazz> pageResult = clazzRepository.findAll(spec, pageable);

        return  pageableMapper.toPageableDto(pageResult);
    }
}
