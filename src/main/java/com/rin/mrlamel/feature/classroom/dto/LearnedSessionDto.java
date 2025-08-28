package com.rin.mrlamel.feature.classroom.dto;

import com.rin.mrlamel.feature.identity.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnedSessionDto {
    SessionDto session;
    List<String> absentStudents;
    List<String> lateStudents;
    List<String> excuseStudents;
}
