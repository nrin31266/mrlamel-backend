package com.rin.mrlamel.feature.classroom.dto;

import com.rin.mrlamel.feature.identity.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentCheckDto {
    private boolean exists;       // user có tồn tại trong hệ thống không
    private boolean canEnroll;    // có thể thêm vào lớp hay không
    private String reason;        // lý do không thể enroll (role/status)
    private User user;         // thông tin user nếu có, null nếu không tìm thấy

}
