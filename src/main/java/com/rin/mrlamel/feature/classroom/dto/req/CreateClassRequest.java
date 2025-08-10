package com.rin.mrlamel.feature.classroom.dto.req;

import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateClassRequest {
    @NotBlank(message = "Class name is required")
    private String name; // Tên lớp hiển thị: "Lớp TOEIC căn bản buổi tối"
    private String avatarUrl; // URL ảnh đại diện lớp
    //    private boolean isActive = true;
//    @NotNull(message = "Start date is required")
//    private LocalDate startDate; // Ngày khai giảng chính thức
    //Default status is ACTIVE
//    private CLASS_STATUS status; // Trạng thái của lớp DRAFT
//    private int actualSessions; // Số buổi học thực tế cho lớp này
    // End
//    private LocalDate endDate;
//    @Column(nullable = false)
    @NotNull(message = "End date is required")
    private Integer totalSessions; // Tổng số buổi học của lớp
    @NotNull(message = "Start date is required")
    private Integer maxSeats; // Số lượng học viên tối đa cho lớp
    //    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
//    private List<ClassSchedule> schedules;
//    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
//    private List<ClassSession> sessions; // Danh sách các buổi học của lớp
    @NotNull(message = "Schedules are required")
    Long courseId; // Khóa học mà lớp này thuộc về
    @NotNull(message = "Room ID is required")
    Long roomId; // ID phòng học mà lớp này sẽ diễn ra
}
