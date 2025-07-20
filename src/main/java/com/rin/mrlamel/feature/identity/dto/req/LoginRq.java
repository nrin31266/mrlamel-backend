package com.rin.mrlamel.feature.identity.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.USER_ROLE;
import com.rin.mrlamel.common.constant.USER_STATUS;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRq {
    String email;
    String password;

}
