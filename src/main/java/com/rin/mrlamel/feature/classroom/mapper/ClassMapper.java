package com.rin.mrlamel.feature.classroom.mapper;

import com.rin.mrlamel.feature.classroom.dto.req.CreateClassRequest;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    Clazz toClass(CreateClassRequest createClassRequest);
}
