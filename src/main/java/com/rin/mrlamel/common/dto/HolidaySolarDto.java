package com.rin.mrlamel.common.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidaySolarDto {
    LocalDate date;
    String name;
    int year; // Năm của ngày lễ
    String originDate; // Ngày gốc của ngày lễ, ví dụ: "01/01" cho ngày 1 tháng 1
    String rootType; // Loại ngày gốc, ví dụ: "solar" hoặc "lunar"
}
