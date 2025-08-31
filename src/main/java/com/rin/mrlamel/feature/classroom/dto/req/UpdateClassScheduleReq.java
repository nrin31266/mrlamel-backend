package com.rin.mrlamel.feature.classroom.dto.req;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateClassScheduleReq {
    @NotBlank(message = "Day of week is required")
    String dayOfWeek;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime  endTime;
//    @NotNull(message = "Room ID is required")
//    private Long roomId;

}
