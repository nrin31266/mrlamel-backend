package com.rin.mrlamel.feature.identity.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRes {
    String accessToken; // JWT access token
    String tokenType = "Bearer"; // Type of the token, typically
}
