package com.rin.mrlamel.feature.classroom.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkClassOnReadyRq {
    List<LocalDate> unavailableDates; // Danh sách các ngày không thể tổ chức lớp
    LocalDate startDate; // Ngày bắt đầu lớp học
}
