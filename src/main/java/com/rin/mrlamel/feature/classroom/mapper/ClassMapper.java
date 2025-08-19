package com.rin.mrlamel.feature.classroom.mapper;

import com.rin.mrlamel.feature.classroom.dto.AttendanceDTO;
import com.rin.mrlamel.feature.classroom.dto.ClazzDto;
import com.rin.mrlamel.feature.classroom.dto.SessionDto;
import com.rin.mrlamel.feature.classroom.dto.TimeTableSessionDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    Clazz toClass(CreateClassRequest createClassRequest);
    ClassSchedule toClassSchedule(CreateClassScheduleReq createClassScheduleReq);
    void updateClassScheduleFromReq(UpdateClassScheduleReq updateClassScheduleReq, @MappingTarget ClassSchedule classSchedule);

    SessionDto toSessionDto(ClassSession classSession);

    AttendanceDTO toAttendanceDTO(Attendance attendance);
    ClazzDto toClazzDTO(Clazz clazz);
    TimeTableSessionDto toTimeTableSessionDto(ClassSession classSession);

}
