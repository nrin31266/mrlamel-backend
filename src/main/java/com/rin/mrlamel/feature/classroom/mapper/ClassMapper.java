package com.rin.mrlamel.feature.classroom.mapper;

import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.dto.req.CreateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateClassScheduleReq;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    Clazz toClass(CreateClassRequest createClassRequest);
    ClassSchedule toClassSchedule(CreateClassScheduleReq createClassScheduleReq);
    void updateClassScheduleFromReq(UpdateClassScheduleReq updateClassScheduleReq, @MappingTarget ClassSchedule classSchedule);
}
