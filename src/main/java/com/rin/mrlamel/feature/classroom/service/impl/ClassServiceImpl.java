package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS;
import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.common.utils.HolidayService;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.dto.req.MarkClassOnReadyRq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.mapper.ClassMapper;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.repository.*;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import com.rin.mrlamel.feature.classroom.service.ClassService;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
    AuthenticationService authenticationService;
    JwtTokenProvider jwtTokenProvider;
    ClassScheduleRepository classScheduleRepository;
    ClassSessionRepository classSessionRepository;
    HolidayService holidayService;

    @Override
    public Clazz createClass(CreateClassRequest createClassReq, Authentication authentication) {
        Clazz clazz = classMapper.toClass(createClassReq);
        clazz.setCourse(
                courseRepository.findById(createClassReq.getCourseId())
                        .orElseThrow(() -> new AppException("Course not found with ID: " + createClassReq.getCourseId()))
        );

        clazz.setStatus(CLASS_STATUS.DRAFT); // Default status
        clazz.setCreatedBy(authenticationService.getUserByAuthentication(authentication));
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

    @Override
    public Clazz getClassById(Long classId) {
        return clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException("Class not found with ID: " + classId));
    }

    @Override
    public ClassSchedule createClassSchedule(CreateClassScheduleReq createClassScheduleReq) {
        if (createClassScheduleReq.getStartTime().isAfter(createClassScheduleReq.getEndTime())) {
            throw new AppException("Start time must be before end time");
        }
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(createClassScheduleReq.getDayOfWeek().toUpperCase());

        Clazz clazz = getClassById(createClassScheduleReq.getClassId());

        if(classScheduleRepository.existsByClazzIdAndDayOfWeekAndTimeOverlap(
                createClassScheduleReq.getClassId(),
                dayOfWeek,
                createClassScheduleReq.getStartTime(),
                createClassScheduleReq.getEndTime()
        )) {
            throw new AppException("Class schedule overlaps with existing schedule");
        }

        ClassSchedule classSchedule = classMapper.toClassSchedule(createClassScheduleReq);
        classSchedule.setDayOfWeek(dayOfWeek);
        classSchedule.setClazz(clazz);
        classSchedule = classScheduleRepository.save(classSchedule);

        return classSchedule;
    }

    @Override
    public ClassSchedule updateClassSchedule(Long classScheduleId, UpdateClassScheduleReq updateClassScheduleReq) {
        if (updateClassScheduleReq.getStartTime().isAfter(updateClassScheduleReq.getEndTime())) {
            throw new AppException("Start time must be before end time");
        }
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(updateClassScheduleReq.getDayOfWeek().toUpperCase());
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new AppException("Class schedule not found with ID: " + classScheduleId));
        Clazz clazz = classSchedule.getClazz();
        if(clazz.getStatus() != CLASS_STATUS.DRAFT) {
            throw new AppException("Cannot update class schedule for a class that is not in DRAFT status");
        }
        if(classScheduleRepository.existsByClazzIdAndDayOfWeekAndTimeOverlap(
                clazz.getId(),
                dayOfWeek,
                updateClassScheduleReq.getStartTime(),
                updateClassScheduleReq.getEndTime()
        )) {
            throw new AppException("Class schedule overlaps with existing schedule");
        }

        classMapper.updateClassScheduleFromReq(updateClassScheduleReq, classSchedule);
        classSchedule.setDayOfWeek(dayOfWeek);
        return classScheduleRepository.save(classSchedule);
    }

    @Override
    public void deleteClassSchedule(Long classScheduleId) {
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new AppException("Class schedule not found with ID: " + classScheduleId));
        Clazz clazz = classSchedule.getClazz();
        if(clazz.getStatus() != CLASS_STATUS.DRAFT) {
            throw new AppException("Cannot delete class schedule for a class that is not in DRAFT status");
        }
        classScheduleRepository.delete(classSchedule);
    }

    @Override
    public Clazz markClassOnReady(Long clazzId,
                                               MarkClassOnReadyRq markClassOnReadyRq) {
        Clazz clazz = getClassById(clazzId);
        if (clazz.getStatus() != CLASS_STATUS.DRAFT) {
            throw new AppException("Cannot mark class as ready when it is not in DRAFT status");
        }
        if (clazz.getSchedules() == null || clazz.getSchedules().isEmpty()) {
            throw new AppException("Cannot mark class as ready when it has no schedules");
        }
        List<ClassSession> sessions = new ArrayList<>();
        int totalSessions = clazz.getTotalSessions();
        List<ClassSchedule> schedules = clazz.getSchedules().stream()
                .sorted(
                        Comparator.comparing(ClassSchedule::getDayOfWeek)
                                .thenComparing(ClassSchedule::getStartTime)
                )
                .toList();
        LocalDate currentDate  = markClassOnReadyRq.getStartDate();
        List<LocalDate> holidays = holidayService.getHolidaysForYear(LocalDate.now().getYear());

        while(sessions.size() < totalSessions){
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
            // Tìm schedule khớp với ngày hiện tại
            for (ClassSchedule schedule: schedules){
                if(sessions.size() >= totalSessions){break;}// Đã đủ số buổi học
                if(schedule.getDayOfWeek().equals(currentDayOfWeek)){
                    if (markClassOnReadyRq.getUnavailableDates() != null &&
                        markClassOnReadyRq.getUnavailableDates().contains(currentDate)) {
                        break;
                    }
                    // Kiểm tra nếu ngày hiện tại là ngày lễ
                    if (holidays.contains(currentDate)) {
                        break; // Bỏ qua ngày lễ
                    }
                    // Tạo ClassSession cho ngày hiện tại
                    ClassSession session = ClassSession.builder()
                            .clazz(clazz)
                            .baseSchedule(schedule)
                            .date(currentDate)
                            .startTime(schedule.getStartTime())
                            .endTime(schedule.getEndTime())
                            .teacher(null)
                            .room(null)
                            .status(CLASS_SECTION_STATUS.NONE)
                            .build();
                    sessions.add(session);
                }
            }
            // Kiểm tra nếu không có schedule nào khớp với ngày hiện tại
            currentDate = currentDate.plusDays(1); // Sang ngày tiếp theo

        }

        clazz.setStatus(CLASS_STATUS.READY);
        clazz.setStartDate(markClassOnReadyRq.getStartDate());
        clazz.getSessions().clear(); // Xoá session cũ
        clazz.getSessions().addAll(sessions); // Thêm session mới
        return clazzRepository.save(clazz);
    }

    @Override
    public List<ClassSession> getClassSessionsByClassId(Long classId) {
        Clazz clazz = getClassById(classId);
        return classSessionRepository.findByClazzId(clazz.getId());
    }
}
