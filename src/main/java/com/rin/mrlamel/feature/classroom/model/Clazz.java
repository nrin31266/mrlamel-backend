package com.rin.mrlamel.feature.classroom.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
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

    @Column(nullable = false, unique = true)
    private String code; // Mã lớp như TOEIC-1, IEL-02

    @Column(nullable = false)
    private String name; // Tên lớp hiển thị: "Lớp TOEIC căn bản buổi tối"

    private String description;

    private String avatarUrl; // URL ảnh đại diện lớp

    private boolean isActive = true;

    @Column(nullable = false)
    private String subject; // Ví dụ: TOEIC, IELTS, Ngữ pháp...

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalSessions; // Tổng số buổi học của lớp

    @CreationTimestamp
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassSchedule> schedules;

    @OneToMany(mappedBy = "clazz", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassSession> sessions; // Danh sách các buổi học của lớp


}
