package com.rin.mrlamel.common.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidayDTO {
    String date;
    String name; // Tên ngày lễ, ví dụ: "Quốc khánh", "Tết Nguyên Đán", "Giỗ tổ Hùng Vương"
    String type; // lunar, solar
}
