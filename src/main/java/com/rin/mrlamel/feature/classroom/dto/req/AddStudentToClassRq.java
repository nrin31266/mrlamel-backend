package com.rin.mrlamel.feature.classroom.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddStudentToClassRq {
    Boolean isPaid = true; // Indicates if the user is a paid member of the class
    Long userId; // ID of the user to be added to the class
    @NotNull(message = "classId is required")
    Long classId; // ID of the class to which the user is being added
    @NotBlank(message = "email is required if userId is not provided")
    String email; // Email of the user to be added, optional if userId is provided

    String fullName; // Full name of the user, optional if userId is provided
}
