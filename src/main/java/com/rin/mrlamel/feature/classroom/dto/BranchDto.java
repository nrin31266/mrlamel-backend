package com.rin.mrlamel.feature.classroom.dto;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDto {
    Long id;
    String name;
    String address;
    String phone;
    Long roomCount;
}
