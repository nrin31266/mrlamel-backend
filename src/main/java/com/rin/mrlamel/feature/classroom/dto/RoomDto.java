package com.rin.mrlamel.feature.classroom.dto;

import com.rin.mrlamel.feature.classroom.model.Branch;
import com.rin.mrlamel.feature.classroom.model.Room;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {
    Long id; // ID của phòng
    String code; // Mã phòng như "A3", "B1", "C2"
    String name; // Ví dụ: "Phòng A3"
    int capacity; // Sức chứa của phòng
   Branch branch;

}
