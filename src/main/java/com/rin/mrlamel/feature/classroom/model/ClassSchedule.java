package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    DayOfWeek dayOfWeek;


    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime  endTime;

//    @ManyToOne
//    @JoinColumn(name = "teacher_id")
//    private User teacher;
//
//    @ManyToOne
//    @JoinColumn(name = "room_id")
//    private Room room;
    @JsonIgnore
    @OneToMany(mappedBy = "baseSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ClassSession> classSessions; // Danh sách các buổi học dựa trên lịch này

}
