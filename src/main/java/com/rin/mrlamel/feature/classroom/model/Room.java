package com.rin.mrlamel.feature.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    int capacity; // Sức chứa của phòng


    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id")
    Branch branch;

//    @JsonIgnore
//    @OneToMany(mappedBy = "room", orphanRemoval = true)
//    List<Clazz> classes; // Danh sách các lớp học trong phòng này

//    @JsonIgnore
//    @OneToMany(mappedBy = "room", orphanRemoval = true)
//    List<ClassSchedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "room", orphanRemoval = true)
    List<ClassSession> sessions;
}
