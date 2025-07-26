package com.rin.mrlamel.feature.identity.dto.req;

import com.rin.mrlamel.common.constant.USER_ROLE;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRq {
    String fullName;
    String phoneNumber;
    LocalDate dob;
    String address;
}
