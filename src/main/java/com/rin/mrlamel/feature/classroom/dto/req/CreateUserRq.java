package com.rin.mrlamel.feature.classroom.dto.req;

import com.rin.mrlamel.common.constant.GENDER;
import com.rin.mrlamel.common.constant.USER_ROLE;
import com.rin.mrlamel.common.constant.USER_STATUS;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRq {
    @NotBlank(message = "Email is required")
    String email;
    @NotBlank(message = "Full name is required")
    String fullName;
    @NotNull(message = "Day of birth number is required")
    LocalDate dob;
}
