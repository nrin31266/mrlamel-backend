package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
public class ClassEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id")
    Clazz clazz; // Lớp học mà học viên đăng ký
    @ManyToOne(optional = false)
    @JoinColumn(name = "attendee_id")
    User attendee; // Học viên đăng ký lớp học này
    LocalDateTime enrolledAt; // Thời gian đăng ký lớp học
    Boolean isPaid; // Trạng thái thanh toán học phí
    Long tuitionFee; // Số tiền học phí đã thanh toán
    Long paidAmount; // Số tiền đã thanh toán
    @JsonIgnore
    @OneToMany(mappedBy = "attendanceEnrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Attendance> attendances; // Danh sách điểm danh của học viên trong lớp học này

}
