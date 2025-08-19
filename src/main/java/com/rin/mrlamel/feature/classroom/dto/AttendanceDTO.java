package com.rin.mrlamel.feature.classroom.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.ATTENDANCE_STATUS;
import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceDTO {
    Long id;
    ATTENDANCE_STATUS status; // Trạng thái điểm danh
    String note; // Ghi chú về trạng thái điểm danh, ví dụ: "Nghỉ có phép", "Đi trễ do kẹt xe", v.v.
    ClassEnrollmentDTO attendanceEnrollment;
    Double absenceCount; // Số buổi học mà học viên không có mặt
}
