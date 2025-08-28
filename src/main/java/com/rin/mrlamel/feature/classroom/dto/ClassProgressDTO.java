package com.rin.mrlamel.feature.classroom.dto;

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
public class ClassProgressDTO {
    ClazzDto clazz;
    List<LearnedSessionDto> learnedSessions;
}
