package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, unique = true)
    private String code; // Mã khóa học, ví dụ: "TOEIC-1", "IELTS-02"
    @Column(nullable = false)
    private String name; // Tên khóa học, ví dụ: "Khóa học TOEIC căn bản buổi tối"
    @Column(nullable = false)

    String logoUrl; // URL ảnh đại diện khóa học
    int totalSessions; // Tổng số buổi học của khóa học
    @Column(nullable = false)
    Long fee; // Học phí của khóa học, lưu dưới dạng Long để dễ dàng xử lý tiền tệ

    Long mrpFee; // MRP (Maximum Retail Price) của khóa học, lưu dưới dạng Long để dễ dàng xử lý tiền tệ
    @CreationTimestamp
    private LocalDateTime createdAt; // Ngày tạo khóa học
    @UpdateTimestamp
    private LocalDateTime updatedAt; // Ngày cập nhật khóa học


    @OneToMany(mappedBy = "course")
    @JsonIgnore
    List<Clazz> classes; // Danh sách các lớp học thuộc khóa học này

    // Check mrpFee is greater than fee
    public boolean isMrpFeeGreaterThanFee() {
        return mrpFee != null && fee != null && mrpFee > fee;
    }
}
