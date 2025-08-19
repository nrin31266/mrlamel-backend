package com.rin.mrlamel.feature.classroom.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.feature.classroom.model.Attendance;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassEnrollmentDTO {
    Long id;
    User attendee; // Học viên đăng ký lớp học này
    LocalDateTime enrolledAt; // Thời gian đăng ký lớp học
    Boolean isPaid; // Trạng thái thanh toán học phí
    Long tuitionFee; // Số tiền học phí đã thanh toán
    Long paidAmount; // Số tiền đã thanh toán
}
