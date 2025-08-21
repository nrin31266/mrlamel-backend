package com.rin.mrlamel.feature.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Permission {
    @Id
    String name; // Tên quyền, ví dụ
    String description; // Mô tả quyền, ví dụ: "Quyền quản lý lớp học", "Quyền xem điểm", v.v.
}
