package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.ATTENDANCE_STATUS;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "attendance_enrollment_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ATTENDANCE_STATUS status; // Trạng thái điểm danh

    String note; // Ghi chú về trạng thái điểm danh, ví dụ: "Nghỉ có phép", "Đi trễ do kẹt xe", v.v.


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    ClassSession session;


    @ManyToOne
    @JoinColumn(name = "attendance_enrollment_id", nullable = false)
    ClassEnrollment attendanceEnrollment;


}
