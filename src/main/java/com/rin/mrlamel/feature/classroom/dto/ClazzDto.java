package com.rin.mrlamel.feature.classroom.dto;

import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.feature.classroom.model.Course;
import com.rin.mrlamel.feature.identity.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClazzDto {
    private Long id;
    private String name;
    private String avatarUrl; // URL ảnh đại diện lớp
    private LocalDate startDate; // Ngày khai giảng chính thức
    private CLASS_STATUS status; // Trạng thái của lớp: ACTIVE, INACTIVE, COMPLETED
    private LocalDate endDate;
    Integer maxSeats; // Số lượng học viên tối đa cho lớp
    LocalDateTime registrationStartTime; // Ngày bắt đầu đăng ký học
    LocalDateTime registrationEndTime; // Ngày kết thúc đăng ký học
    private Integer totalSessions; // Tổng số buổi học của lớp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Course course; // Khóa học mà lớp này thuộc về
    private User createdBy; // Người tạo lớp học, có thể là giáo viên hoặc quản trị viên
}
