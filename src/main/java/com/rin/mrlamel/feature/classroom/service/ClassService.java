package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.classroom.dto.*;
import com.rin.mrlamel.feature.classroom.dto.req.*;
import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.identity.model.User;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClassService {
    Clazz createClass(CreateClassRequest createClassReq, Authentication authentication);
    PageableDto<Clazz> getAllClasses(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String status,
            String searchTerm
    );
    Clazz getClassById(Long classId);
    void removeClass(Long classId);
    ClassSchedule createClassSchedule(CreateClassScheduleReq createClassScheduleReq);
    ClassSchedule updateClassSchedule(Long classScheduleId, UpdateClassScheduleReq createClassScheduleReq);
    void deleteClassSchedule(Long classScheduleId);
    Clazz markClassOnReady(Long clazzId, MarkClassOnReadyRq markClassOnReadyRq);
    List<ClassSession> getClassSessionsByClassId(Long classId);
    List<ClassSession> getClassSessionsByClassScheduleId(Long classScheduleId);
    ClassSession getClassSessionById(Long classSessionId);


    ClassSchedule getClassScheduleById(Long classScheduleId);
    CheckStudentDto checkStudentBeforeAddingToClass(String studentEmail, Long classId);
    ClassEnrollment addStudentToClass(AddStudentToClassRq addStudentToClassRq);
    void removeStudentFromClass(Long classId, Long studentId);
    List<ClassEnrollment> getClassEnrollmentsByClassId(Long classId);

    List<TimeTableSessionDto> getTimeTableForTeacherByDay(Long teacherId, LocalDate date);
    TimeTableByWeekDto getTimeTableForTeacherByWeek(Long teacherId, int weekNumber);
//    User empowerClassForTeacher(Long classId, String email);
//    void revokeEmpowermentFromClass(Long classId, Long teacherId);

    List<ClazzDto> getClassesTeacherIsTeaching(Long teacherId);
    List<ClazzDto> getClassesTeacherIsManaging(Long teacherId, int page,
                                            int size,
                                            String sortBy,
                                            String sortDirection,
                                            String status,
                                            String searchTerm);
    void learnSession(Long classSessionId, String content,Authentication authentication);


    List<TimeTableSessionDto> getTimeTableForStudentByDay(Long studentId, LocalDate date);
    TimeTableByWeekDto getTimeTableForStudentByWeek(Long studentId, int weekNumber);

    // Admin xem tất cả thời khóa biểu hôm nay
    List<TimeTableSessionDto> getFullCourseTimeTable();
    // Admin xem tất cả các buổi học đã bị bỏ lỡ trong khoảng thời gian nhất định (vd: 7 ngày gần đây)
    List<TimeTableSessionDto> getMissedSessions(Integer daysAgo);

    List<LearnedSessionDto> getLearnedSessionsForClass(Long classId);



}
