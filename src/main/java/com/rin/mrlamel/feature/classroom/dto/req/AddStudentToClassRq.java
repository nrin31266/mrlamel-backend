package com.rin.mrlamel.feature.classroom.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddStudentToClassRq {
    @NotNull(message = "Is Paid is required")
    Boolean isPaid; // Indicates if the user is a paid member of the class
    Long userId; // ID of the user to be added to the class
    @NotNull(message = "classId is required")
    Long classId; // ID of the class to which the user is being added
    CreateUserRq user; // Assuming CreateUserRq is a class that contains user details
}
