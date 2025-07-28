package com.rin.mrlamel.feature.classroom.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBranchReq {
    @NotBlank(message = "Branch name is required")
    @Length(max = 100, message = "Branch name must not exceed 100 characters")
    @Length(min = 3, message = "Branch name must be at least 3 characters long")
    String name;
    @NotBlank(message = "Address is required")
    @Length(max = 100, message = "Address must not exceed 100 characters")
    @Length(min = 10, message = "Address must be at least 3 characters long")
    String address;
    @NotBlank(message = "Phone name is required")
    @Length(max = 100, message = "Phone must not exceed 100 characters")
    @Length(min = 10, message = "Phone must be at least 3 characters long")
    String phone;
}
