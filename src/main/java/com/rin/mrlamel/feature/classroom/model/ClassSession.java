package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.CLASS_SECTION_STATUS;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClassSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id")
    private ClassSchedule baseSchedule;

    @Column(nullable = false)
    private LocalDate date; // Ngày diễn ra buổi học

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String note; // VD: “Buổi bù do 2/9 nghỉ lễ”, “Giáo viên dạy thay”

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CLASS_SECTION_STATUS status = CLASS_SECTION_STATUS.NONE;

    @CreationTimestamp
    private LocalDate createdAt;
}
