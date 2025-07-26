package com.rin.mrlamel.feature.identity.dto.req;

import com.rin.mrlamel.common.constant.USER_ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

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
    @Email(message = "Invalid email format")
    String email;
    @NotBlank(message = "Password is required")
    @Length(min = 3, message = "Password must be at least 3 characters long")
    String password;
    @NotBlank(message = "Device ID is required")
    String deviceId;
    String deviceName;
}
