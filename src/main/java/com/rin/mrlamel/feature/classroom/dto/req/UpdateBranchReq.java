package com.rin.mrlamel.feature.classroom.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBranchReq {
    String name;
    String address;
    String phone;
}
