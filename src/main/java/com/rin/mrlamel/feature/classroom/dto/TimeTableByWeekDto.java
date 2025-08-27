package com.rin.mrlamel.feature.classroom.dto;

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
public class TimeTableByWeekDto {
    List<TimeTableSessionDto> sessions; // Danh sách các buổi học trong tuần
    Integer weekNumber; // Số thứ tự của tuần trong năm (0 là tuần hiện tại)
    LocalDate weekStartDate; // Ngày bắt đầu của tuần (định dạng: yyyy-MM-dd)
    LocalDate weekEndDate; // Ngày kết thúc của tuần (định dạng: yyyy-MM-dd)
}
