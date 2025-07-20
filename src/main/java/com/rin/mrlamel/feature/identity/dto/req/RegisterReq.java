package com.rin.mrlamel.feature.identity.dto.req;

import com.rin.mrlamel.common.constant.USER_ROLE;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterReq {
    String fullName;
    String phoneNumber;
    LocalDate dob;
    USER_ROLE role;
    String avatarUrl;
    String address;
    String email;
    String password;
    @NotBlank(message = "Device ID is required")
    String deviceId;
    String deviceName;
}
