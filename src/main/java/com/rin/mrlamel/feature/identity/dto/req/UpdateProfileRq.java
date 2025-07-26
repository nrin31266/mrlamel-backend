package com.rin.mrlamel.feature.identity.dto.req;

import com.rin.mrlamel.common.constant.GENDER;
import com.rin.mrlamel.common.constant.USER_ROLE;
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
public class UpdateProfileRq {
    @NotBlank(message = "Full name is required")
    String fullName;
    @NotBlank(message = "Phone number is required")
    String phoneNumber;
    @NotNull(message = "Date of birth is required")
    LocalDate dob;
    @NotBlank(message = "Address is required")
    String address;
    @NotNull(message = "Gender is required")
    GENDER gender;
}
