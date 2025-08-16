package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.feature.classroom.dto.req.*;
import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.dto.StudentCheckDto;
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

    ClassSchedule createClassSchedule(CreateClassScheduleReq createClassScheduleReq);
    ClassSchedule updateClassSchedule(Long classScheduleId, UpdateClassScheduleReq createClassScheduleReq);

    void deleteClassSchedule(Long classScheduleId);

    Clazz markClassOnReady(Long clazzId, MarkClassOnReadyRq markClassOnReadyRq);

    List<ClassSession> getClassSessionsByClassId(Long classId);
    List<ClassSession> getClassSessionsByClassScheduleId(Long classScheduleId);
    ClassSession getClassSessionById(Long classSessionId);
    void assignRoomToSchedules(Long roomId, List<ClassSchedule> classSchedules);
    void assignTeacherToSchedules(Long teacherId, List<ClassSchedule> classSchedules);
    ClassSchedule getClassScheduleById(Long classScheduleId);

    StudentCheckDto checkStudentBeforeAddingToClass(String studentEmail, Long classId);

    ClassEnrollment addStudentToClass(AddStudentToClassRq addStudentToClassRq);

}
