package com.rin.mrlamel.feature.ai.dto;

import com.rin.mrlamel.common.constant.USER_ROLE;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    String fullName;
    String email;
    String role;
}
