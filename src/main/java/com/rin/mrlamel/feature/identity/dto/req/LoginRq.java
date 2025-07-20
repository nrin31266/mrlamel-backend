package com.rin.mrlamel.feature.identity.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRq {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;
    @NotBlank(message = "Password is required")
    @Length(min = 2, message = "Password must be at least 1 characters long")
    String password;
    @NotBlank(message = "Device ID is required")
    String deviceId;
    String deviceName;

}
