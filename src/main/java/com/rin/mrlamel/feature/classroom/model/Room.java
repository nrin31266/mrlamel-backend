package com.rin.mrlamel.feature.classroom.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(nullable = false, unique = true)
    String code; // Mã phòng như "A3", "B1", "C2"
    @Column(nullable = false)
    String name; // Ví dụ: "Phòng A3"

    int capacity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id")
    Branch branch;

    @OneToMany(mappedBy = "room", orphanRemoval = true)
    List<ClassSchedule> schedules;

    @OneToMany(mappedBy = "room", orphanRemoval = true)
    List<ClassSession> sessions;
}
