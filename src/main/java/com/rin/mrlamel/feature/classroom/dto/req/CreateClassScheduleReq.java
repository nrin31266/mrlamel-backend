package com.rin.mrlamel.feature.classroom.dto.req;

import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.model.Room;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateClassScheduleReq {

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "class_id")
//    private Clazz clazz;

//    MONDAY,
//    /**
//     * The singleton instance for the day-of-week of Tuesday.
//     * This has the numeric value of {@code 2}.
//     */
//    TUESDAY,
//    /**
//     * The singleton instance for the day-of-week of Wednesday.
//     * This has the numeric value of {@code 3}.
//     */
//    WEDNESDAY,
//    /**
//     * The singleton instance for the day-of-week of Thursday.
//     * This has the numeric value of {@code 4}.
//     */
//    THURSDAY,
//    /**
//     * The singleton instance for the day-of-week of Friday.
//     * This has the numeric value of {@code 5}.
//     */
//    FRIDAY,
//    /**
//     * The singleton instance for the day-of-week of Saturday.
//     * This has the numeric value of {@code 6}.
//     */
//    SATURDAY,
//    /**
//     * The singleton instance for the day-of-week of Sunday.
//     * This has the numeric value of {@code 7}.
//     */
//    SUNDAY;
    @NotNull(message = "Class ID is required")
    private Long classId;
    @NotBlank(message = "Day of week is required")
    String dayOfWeek;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime  endTime;
//    @NotNull(message = "Teacher ID is required")
//    private Long teacherId;
//    @NotNull(message = "Room ID is required")
//    private Long roomId;
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "teacher_id")
//    private User teacher;


//    @ManyToOne(optional = false)
//    @JoinColumn(name = "room_id")
//    private Room room;


}