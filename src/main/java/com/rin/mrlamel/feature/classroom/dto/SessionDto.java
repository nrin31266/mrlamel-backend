package com.rin.mrlamel.feature.classroom.dto;

import com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS;
import com.rin.mrlamel.feature.identity.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionDto {
    private Long id;
//    private ClazzDto clazz;
//    private ClassSchedule baseSchedule;
    private LocalDate date; // Ngày diễn ra buổi học
    private LocalTime startTime;
    private LocalTime endTime;
    private String note; // VD: “Buổi bù do 2/9 nghỉ lễ”, “Giáo viên dạy thay”
    private User teacher;
    private CLASS_SECTION_STATUS status = CLASS_SECTION_STATUS.NOT_YET;
    private LocalDate createdAt;
    RoomDto room; // Phòng học
    String content; // Nội dung buổi học, có thể là video, tài liệu, v.v.
}
