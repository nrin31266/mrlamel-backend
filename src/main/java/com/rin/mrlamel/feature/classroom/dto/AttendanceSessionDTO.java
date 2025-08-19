package com.rin.mrlamel.feature.classroom.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceSessionDTO {
    ClazzDto clazz;
    SessionDto session;
    List<AttendanceDTO> attendances;
}
