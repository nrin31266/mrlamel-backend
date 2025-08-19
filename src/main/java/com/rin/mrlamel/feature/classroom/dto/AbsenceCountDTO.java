package com.rin.mrlamel.feature.classroom.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbsenceCountDTO {
    Long attendanceId; // ID của điểm danh
    Double absenceCount; // Số buổi học mà học viên không có mặt
}
