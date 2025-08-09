package com.rin.mrlamel.feature.identity.dto.req;

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
public class UpdateUserReq {
    @NotBlank(message = "Email is required")
    String email;
    //    String password;
    @NotBlank(message = "Full name is required")
    String fullName;
    @NotBlank(message = "Phone number is required")
    String phoneNumber;
    @NotNull(message = "Date of birth is required")
    LocalDate dob;
    boolean isActive = false; // Changed from Boolean to primitive boolean for better performance
    @NotBlank(message = "Device ID is required")
    USER_STATUS status = USER_STATUS.OK; // Default status is OK
    String avatarUrl;
    @NotBlank(message = "Address is required")
    String address;
    //    boolean completedProfile = false; // Indicates if the user has completed their profile
    @NotNull(message = "Gender is required")
    GENDER gender;
}
