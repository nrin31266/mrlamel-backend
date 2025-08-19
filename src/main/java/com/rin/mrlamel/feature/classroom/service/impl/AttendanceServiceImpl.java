package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.common.constant.ATTENDANCE_STATUS;
import com.rin.mrlamel.common.constant.USER_ROLE;
import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.classroom.dto.AbsenceCountDTO;
import com.rin.mrlamel.feature.classroom.dto.AttendanceDTO;
import com.rin.mrlamel.feature.classroom.dto.AttendanceSessionDTO;
import com.rin.mrlamel.feature.classroom.mapper.ClassMapper;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.repository.AttendanceRepository;
import com.rin.mrlamel.feature.classroom.repository.ClassSessionRepository;
import com.rin.mrlamel.feature.classroom.repository.ClazzRepository;
import com.rin.mrlamel.feature.classroom.service.AttendanceService;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AttendanceServiceImpl implements AttendanceService {
    ClazzRepository clazzRepository;
    AttendanceRepository attendanceRepository;
    ClassSessionRepository classSessionRepository;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;
    ClassMapper classMapper;


    @Override
    public AttendanceSessionDTO getAttendancesBySessionId(Long sessionId, Authentication authentication) {
        ClassSession classSession = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException("Class session not found"));
        if (!permissionCheckForSessions(authentication, classSession)) {
            throw new AppException("You do not have permission to access this session");
        }
        List<Attendance> attendances = attendanceRepository.findBySessionId(sessionId);
        List<AbsenceCountDTO> absenceCounts = attendanceRepository.countAbsences(
                attendances.stream()
                        .map(attendance -> attendance.getAttendanceEnrollment().getId())
                        .toList(),
                LocalDate.now()
        );

        Map<Long, Double> absenceCountMap = absenceCounts.stream()
                .collect(Collectors.toMap(AbsenceCountDTO::getAttendanceId, AbsenceCountDTO::getAbsenceCount));
        return AttendanceSessionDTO.builder()
                .clazz(classMapper.toClazzDTO(classSession.getClazz()))
                .session(classMapper.toSessionDto(classSession))
                .attendances(attendances.stream()
                        .map(attendance -> {
                            AttendanceDTO attendanceDTO = classMapper.toAttendanceDTO(attendance);
                            Double absenceCount = absenceCountMap.get(attendance.getAttendanceEnrollment().getId());
                            attendanceDTO.setAbsenceCount(absenceCount != null ? absenceCount : 0.0);
                            return attendanceDTO;
                        })
                        .toList())
                .build();
    }

    @Override
    public AttendanceDTO markStatusAttendance(Long attendanceId, ATTENDANCE_STATUS status, Authentication authentication) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AppException("Attendance record not found"));
        ClassSession classSession = attendance.getSession();

        if (!permissionCheckForSessions(authentication, classSession)) {
            throw new AppException("You do not have permission to mark attendance for this session");
        }

        // Check if the status is valid
        if (status == null) {
            throw new AppException("Invalid attendance status");
        }

        // Update the attendance status
        attendance.setStatus(status);
        attendance = attendanceRepository.save(attendance);
        AttendanceDTO attendanceDTO = classMapper.toAttendanceDTO(attendance);
        attendanceDTO.setAbsenceCount(
            attendanceRepository.countAbsence(attendance.getAttendanceEnrollment().getId(), LocalDate.now()).getAbsenceCount()
        );
        return attendanceDTO;
    }

    private boolean permissionCheckForSessions(Authentication authentication, ClassSession classSession) {
        Long userId = (Long) jwtTokenProvider.getClaim(authentication, "id");
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new AppException("User not found");
        }
        if(user.getRole().equals(USER_ROLE.ADMIN)) {
            return true; // Admins can access all sessions
        }
        if (classSession.getTeacher().getId().equals(userId) ||
            classSession.getClazz().getManagers().contains(user)) {
            return true; // Teachers can access sessions they teach, and managers can access sessions of their classes
        }

        return false;
    }
}
