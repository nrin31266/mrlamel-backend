package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.common.constant.*;
import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.common.utils.HolidayService;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.classroom.dto.*;
import com.rin.mrlamel.feature.classroom.dto.req.*;
import com.rin.mrlamel.feature.classroom.mapper.ClassMapper;
import com.rin.mrlamel.feature.classroom.model.*;
import com.rin.mrlamel.feature.classroom.repository.*;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.identity.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    UserService userService;
    ClassScheduleRepository classScheduleRepository;
    ClassSessionRepository classSessionRepository;
    HolidayService holidayService;
    ClassEnrollmentRepository classEnrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    JwtTokenProvider jwtTokenProvider;

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
    public PageableDto<Clazz> getAllClasses(int page, int size, String sortBy, String sortDirection, String status,
                                            String searchTerm) {
        Specification<Clazz> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern)
                ));
            }
//            // teacherId filter bằng subquery EXISTS
//            if (teacherId != null && !teacherId.isEmpty()) {
//                Long teacherIdLong = Long.parseLong(teacherId);
//
//                Subquery<Integer> subquery = query.subquery(Integer.class);
//                Root<ClassSession> sessionRoot = subquery.from(ClassSession.class);
//                subquery.select(criteriaBuilder.literal(1));
//                subquery.where(
//                        criteriaBuilder.equal(sessionRoot.get("clazz"), root),
//                        criteriaBuilder.equal(sessionRoot.get("teacher").get("id"), teacherIdLong)
//                );
//
//                predicates.add(criteriaBuilder.exists(subquery));
//
//            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(page, size, sort);
        Page<Clazz> pageResult = clazzRepository.findAll(spec, pageable);

        return pageableMapper.toPageableDto(pageResult);
    }

    @Override
    public Clazz getClassById(Long classId) {
        return clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException("Class not found with ID: " + classId));
    }

    @Override
    public void removeClass(Long classId) {
        Clazz clazz = getClassById(classId);
        if (clazz.getStatus() != CLASS_STATUS.DRAFT) {
            throw new AppException("Cannot remove class that is not in DRAFT status");
        }
        // Xoá tất cả lịch học và buổi học liên quan
        classScheduleRepository.deleteAll(clazz.getSchedules());
        classSessionRepository.deleteAll(clazz.getSessions());
        classEnrollmentRepository.deleteAll(clazz.getEnrollments());
        clazzRepository.delete(clazz);
    }

    @Override
    public ClassSchedule createClassSchedule(CreateClassScheduleReq createClassScheduleReq) {
        if (createClassScheduleReq.getStartTime().isAfter(createClassScheduleReq.getEndTime())) {
            throw new AppException("Start time must be before end time");
        }
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(createClassScheduleReq.getDayOfWeek().toUpperCase());

        Clazz clazz = getClassById(createClassScheduleReq.getClassId());

        if (classScheduleRepository.existsByClazzIdAndDayOfWeekAndTimeOverlap(
                createClassScheduleReq.getClassId(),
                dayOfWeek,
                createClassScheduleReq.getStartTime(),
                createClassScheduleReq.getEndTime(),
                null
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
        if (clazz.getStatus() != CLASS_STATUS.DRAFT) {
            throw new AppException("Cannot update class schedule for a class that is not in DRAFT status");
        }
        if (classScheduleRepository.existsByClazzIdAndDayOfWeekAndTimeOverlap(
                clazz.getId(),
                dayOfWeek,
                updateClassScheduleReq.getStartTime(),
                updateClassScheduleReq.getEndTime(),
                classScheduleId // Pass the current schedule ID to avoid checking against itself
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
        if (clazz.getStatus() != CLASS_STATUS.DRAFT) {
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
        LocalDate currentDate = markClassOnReadyRq.getStartDate();

        // Ước lượng endDate dựa trên số session và schedule, dư hẳn 1 năm cho an toàn
        LocalDate estimatedEndDate = currentDate.plusWeeks(totalSessions / schedules.size() + 1);
        List<Integer> years = new ArrayList<>();
        for (int y = currentDate.getYear(); y <= estimatedEndDate.getYear() + 1; y++) {
            years.add(y);
        }
        Set<LocalDate> holidays = holidayService.getHolidayDatesForYears(years);


        while (sessions.size() < totalSessions) {
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
            // Tìm schedule khớp với ngày hiện tại
            for (ClassSchedule schedule : schedules) {
                if (sessions.size() >= totalSessions) {
                    break;
                }// Đã đủ số buổi học
                if (schedule.getDayOfWeek().equals(currentDayOfWeek)) {
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
                            .status(CLASS_SECTION_STATUS.NOT_YET)
                            .build();
                    sessions.add(session);
                }
            }
            // Kiểm tra nếu không có schedule nào khớp với ngày hiện tại
            currentDate = currentDate.plusDays(1); // Sang ngày tiếp theo

        }
        LocalDate now = LocalDate.now();
        clazz.setStatus(markClassOnReadyRq.getStartDate().isAfter(now) ? CLASS_STATUS.READY : CLASS_STATUS.ONGOING);
        clazz.setStartDate(markClassOnReadyRq.getStartDate());
        clazz.setEndDate(sessions.getLast().getDate().plusDays(1)); // Ngày kết thúc dự kiến là ngày sau buổi cuối cùng
        clazz.getSessions().clear(); // Xoá session cũ
        clazz.getSessions().addAll(sessions); // Thêm session mới
        return clazzRepository.save(clazz);
    }

    @Override
    public List<ClassSession> getClassSessionsByClassId(Long classId) {
        Clazz clazz = getClassById(classId);
        return classSessionRepository.findByClazzId(clazz.getId());
    }

    @Override
    public List<ClassSession> getClassSessionsByClassScheduleId(Long classScheduleId) {
        return classSessionRepository.findByBaseScheduleId(classScheduleId);
    }

    @Override
    public ClassSession getClassSessionById(Long classSessionId) {
        return classSessionRepository.findById(classSessionId)
                .orElseThrow(() -> new AppException("Class session not found with ID: " + classSessionId));
    }


    @Override
    public ClassSchedule getClassScheduleById(Long classScheduleId) {
        return classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new AppException("Class schedule not found with ID: " + classScheduleId));
    }

    @Override
    public CheckStudentDto checkStudentBeforeAddingToClass(String studentEmail, Long classId) {
        boolean exists;       // user có tồn tại trong hệ thống không
        boolean canEnroll;    // có thể thêm vào lớp hay không
        String reason;        // lý do không thể enroll (role/status)
        User user;         // thông tin user nếu có, null nếu không tìm thấy
        Clazz clazz = getClassById(classId); // Có thể throw AppException nếu không tìm thấy lớp

        try {
            user = userService.getUserByEmail(studentEmail);
            if (user == null) {
                exists = false;
                canEnroll = true;
                reason = "Student not found with email: " + studentEmail;
            } else if (!user.getRole().equals(USER_ROLE.STUDENT)) {
                exists = true;
                canEnroll = false;
                reason = "User is not a student";
            } else if (user.getStatus() != USER_STATUS.OK) {
                exists = true;
                canEnroll = false;
                reason = "The status of user is " + user.getStatus() + ", cannot enroll";
            } else if (classEnrollmentRepository.existsByClazzIdAndAttendeeId(clazz.getId(), user.getId())) {
                exists = true;
                canEnroll = false;
                reason = "User is already enrolled in this class";
            } else if (clazz.getStatus() == CLASS_STATUS.DRAFT || clazz.getStatus() == CLASS_STATUS.FINISHED || clazz.getStatus() == CLASS_STATUS.CANCELLED) {
                exists = true;
                canEnroll = false;
                reason = "Class is not in a valid state for enrollment: " + clazz.getStatus();
            } else {
                List<Clazz> classesConflict = clazzRepository.findClassesWithFutureSessionConflict(clazz.getId(), user.getId());
                log.info("Checking conflicts for user {} in class {}: found {} conflicts", user.getEmail(), clazz.getName(), classesConflict.size());
                if (!classesConflict.isEmpty()) {
                    exists = true;
                    canEnroll = false;
                    reason = "User has future class sessions that conflict with this class: " + classesConflict.stream()
                            .map(Clazz::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("Unknown classes") + ". Total conflict: " + classesConflict.size() + ""
                             + ". Please check the schedule of the user";
                } else {
                    exists = true;
                    canEnroll = true;
                    reason = "User can be enrolled";
                }
            }
        } catch (AppException e) {
            user = null;
            exists = false;
            reason = "Student not found with email: " + studentEmail;
            canEnroll = false;
        }
        return CheckStudentDto.builder()
                .exists(exists)
                .user(user)
                .canEnroll(canEnroll)
                .reason(reason)
                .build();
    }

    @Override
    @Transactional
    public ClassEnrollment addStudentToClass(AddStudentToClassRq addStudentToClassRq) {
        User user;
        if (addStudentToClassRq.getUserId() == null) {
            if (addStudentToClassRq.getEmail() == null ||
                addStudentToClassRq.getFullName() == null) {
                throw new AppException("User information is required to create a new student");
            }
            // Thêm user vào hệ thống
            user = userService.createUser(User.builder()
                    .email(addStudentToClassRq.getEmail())
                    .fullName(addStudentToClassRq.getFullName())
                    .status(USER_STATUS.OK)
                    .isActive(true)
                    .role(USER_ROLE.STUDENT)
                    .build());
        } else {
            // Kiểm tra user trước khi thêm vào lớp
            CheckStudentDto checkResult = checkStudentBeforeAddingToClass(
                    addStudentToClassRq.getEmail(),
                    addStudentToClassRq.getClassId()
            );
            if (!checkResult.isExists() || !checkResult.isCanEnroll()) {
                throw new AppException(checkResult.getReason());
            }
            user = checkResult.getUser();
        }

        Clazz clazz = getClassById(addStudentToClassRq.getClassId());

        // Tạo enrollment
        ClassEnrollment classEnrollment = ClassEnrollment.builder()
                .clazz(clazz)
                .attendee(user)
                .enrolledAt(LocalDateTime.now())
                .build();

        // Save enrollment trước để có ID
        classEnrollmentRepository.save(classEnrollment);

        // Tạo attendances
        List<Attendance> attendances = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (ClassSession session : clazz.getSessions()) {
            ATTENDANCE_STATUS status;
            if (session.getDate().isBefore(today)) {
                status = ATTENDANCE_STATUS.NOT_JOINED_YET;
            } else {
                status = ATTENDANCE_STATUS.PRESENT;
            }

            attendances.add(
                    Attendance.builder()
                            .session(session)
                            .attendanceEnrollment(classEnrollment) // <-- nên dùng quan hệ qua enrollment
                            .status(status)
                            .build()
            );
        }

        // Save attendances
        attendanceRepository.saveAll(attendances);

        return classEnrollment;
    }

    @Override
    public void removeStudentFromClass(Long classId, Long studentId) {
        Clazz clazz = getClassById(classId);
        ClassEnrollment classEnrollment = classEnrollmentRepository.findByClazzIdAndAttendeeId(clazz.getId(), studentId)
                .orElseThrow(() -> new AppException("Student not found in this class"));


        // Xoá enrollment, sẽ tự động xoá các attendance liên quan do orphanRemoval
        classEnrollmentRepository.delete(classEnrollment);
    }

    @Override
    public List<ClassEnrollment> getClassEnrollmentsByClassId(Long classId) {
        Clazz clazz = getClassById(classId);
        return classEnrollmentRepository.findByClazzId(clazz.getId());
    }

    @Override
    public List<TimeTableSessionDto> getTimeTableForTeacherByDay(Long teacherId, LocalDate date) {
        return classSessionRepository.findTimeTableForTeacherByDay(teacherId, date)
                .stream()
                .map(classMapper::toTimeTableSessionDto)
                .toList();
    }

    @Override
    public TimeTableByWeekDto getTimeTableForTeacherByWeek(Long teacherId, int weekNumber) {

        LocalDate now = LocalDate.now();
        // 0 is the current week, 1 is next week, etc.
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY).plusWeeks(weekNumber);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return TimeTableByWeekDto.builder()
                .weekStartDate(startOfWeek)
                .weekEndDate(endOfWeek)
                .weekNumber(weekNumber)
                .sessions(
                        classSessionRepository.findTimeTableForTeacherByWeek(teacherId, startOfWeek, endOfWeek)
                                .stream()
                                .map(classMapper::toTimeTableSessionDto)
                                .toList()
                )
                .build();
    }

//    @Override
//    public User empowerClassForTeacher(Long classId, String email) {
//        Clazz clazz = getClassById(classId);
//        User teacher = userService.getUserByEmail(email);
//        if (teacher == null || !teacher.getRole().equals(USER_ROLE.TEACHER)) {
//            throw new AppException("Teacher not found with email: " + email);
//        }
//        if (!clazz.getManagers().contains(teacher)) {
//            throw new AppException("Teacher is already a manager of this class");
//        }
//        return teacher;
//    }
//
//    @Override
//    public void revokeEmpowermentFromClass(Long classId, Long teacherId) {
//        Clazz clazz = getClassById(classId);
//        User teacher = userService.getUserById(teacherId);
//        if (teacher == null || !teacher.getRole().equals(USER_ROLE.TEACHER)) {
//            throw new AppException("Teacher not found with ID: " + teacherId);
//        }
//        if (!clazz.getManagers().remove(teacher)) {
//            throw new AppException("Teacher is not a manager of this class");
//        }
//        clazzRepository.save(clazz);
//    }

    @Override
    public List<ClazzDto> getClassesTeacherIsTeaching(Long teacherId) {
        return clazzRepository.getClassesTeacherIsTeaching(teacherId)
                .stream()
                .map(classMapper::toClazzDTO)
                .toList();
    }

    @Override
    public List<ClazzDto> getClassesTeacherIsManaging(Long teacherId, int page, int size, String sortBy, String sortDirection, String status, String searchTerm) {
        return List.of();
    }


    @Override
    public void learnSession(Long classSessionId, String content, Authentication authentication) {
        ClassSession classSession = getClassSessionById(classSessionId);
        if (classSession.getStatus() != CLASS_SECTION_STATUS.NOT_YET) {
            throw new AppException("Cannot learn session that is not in NOT_YET status");
        }
        if(permissionCheckForSessions(authentication, classSession)) {
            throw new AppException("You do not have permission mark learn for this session");
        }


        classSession.setContent(content);
        classSession.setStatus(CLASS_SECTION_STATUS.DONE);
        classSessionRepository.save(classSession);
    }

    @Override
    public List<TimeTableSessionDto> getTimeTableForStudentByDay(Long studentId, LocalDate date) {
        User student = userService.getUserById(studentId);
        if (student == null || !student.getRole().equals(USER_ROLE.STUDENT)) {
            throw new AppException("Student not found with ID: " + studentId);
        }

        return classSessionRepository.findTimeTableForStudentByDay(studentId, date)
                .stream()
                .map(classMapper::toTimeTableSessionDto)
                .toList();
    }

    @Override
    public TimeTableByWeekDto getTimeTableForStudentByWeek(Long studentId, int weekNumber) {
        User student = userService.getUserById(studentId);
        if (student == null || !student.getRole().equals(USER_ROLE.STUDENT)) {
            throw new AppException("Student not found with ID: " + studentId);
        }

        LocalDate now = LocalDate.now();
        // 0 is the current week, 1 is next week, etc.
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY).plusWeeks(weekNumber);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return TimeTableByWeekDto.builder()
                .weekStartDate(startOfWeek)
                .weekEndDate(endOfWeek)
                .weekNumber(weekNumber)
                .sessions(
                        classSessionRepository.findTimeTableForStudentByWeek(studentId, startOfWeek, endOfWeek)
                                .stream()
                                .map(classMapper::toTimeTableSessionDto)
                                .toList()
                )
                .build();
    }

    private boolean permissionCheckForSessions(Authentication authentication, ClassSession classSession) {
        Long userId = (Long) jwtTokenProvider.getClaim(authentication, "id");
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new AppException("User not found");
        }
        if(user.getRole().equals(USER_ROLE.ADMIN)) {
            return false; // Admins can access all sessions
        }
        if (classSession.getTeacher().getId().equals(userId)) {
            return false; // Teachers can access sessions they teach, and managers can access sessions of their classes
        }

        return true;
    }
}
