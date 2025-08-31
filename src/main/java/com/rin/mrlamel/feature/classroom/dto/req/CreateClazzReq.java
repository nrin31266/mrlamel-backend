package com.rin.mrlamel.feature.classroom.dto.req;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateClazzReq {
    private String code; // Mã lớp như TOEIC-1, IEL-02
    private String name; // Tên lớp hiển thị: "Lớp TOEIC căn bản buổi tối"
    private String description;
    private String avatarUrl; // URL ảnh đại diện lớp
    private String subject; // Ví dụ: TOEIC, IELTS, Ngữ pháp...
    private LocalDate expectedStartDate; // Ngày dự kiến mở lớp
    Integer totalSessions; // Tổng số buổi học của lớp

}
