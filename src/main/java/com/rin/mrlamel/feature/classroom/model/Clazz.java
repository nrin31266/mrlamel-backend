package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name; // Tên lớp hiển thị: "Lớp TOEIC căn bản buổi tối"


    private String avatarUrl; // URL ảnh đại diện lớp

//    private boolean isActive = true;


    private LocalDate startDate; // Ngày khai giảng chính thức
    @Enumerated(EnumType.STRING)
    private CLASS_STATUS status; // Trạng thái của lớp: ACTIVE, INACTIVE, COMPLETED
//    @Column(nullable = false)
//    private int actualSessions; // Số buổi học thực tế cho lớp này

    // End
    private LocalDate endDate;

    Integer maxSeats; // Số lượng học viên tối đa cho lớp

    LocalDateTime registrationStartTime; // Ngày bắt đầu đăng ký học
    LocalDateTime registrationEndTime; // Ngày kết thúc đăng ký học

    @Column(nullable = false)
    private Integer totalSessions; // Tổng số buổi học của lớp

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = false)
//    private Room room; // Khóa học mà lớp này thuộc về

    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassSchedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassSession> sessions; // Danh sách các buổi học của lớp

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassEnrollment> enrollments; // Danh sách học viên đã đăng ký lớp này

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Khóa học mà lớp này thuộc về


    @ManyToOne
    @JoinColumn(name = "create_by_id", nullable = false)
    private User createdBy; // Người tạo lớp học, có thể là giáo viên hoặc quản trị viên


}
