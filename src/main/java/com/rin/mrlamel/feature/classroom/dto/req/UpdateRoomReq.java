package com.rin.mrlamel.feature.classroom.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoomReq {
    String code; // Mã phòng như "A3", "B1", "C2"
    String name; // Ví dụ: "Phòng A3"
    int capacity; // Sức chứa của phòng
    Long branchId; // ID của chi nhánh mà phòng thuộc về
}