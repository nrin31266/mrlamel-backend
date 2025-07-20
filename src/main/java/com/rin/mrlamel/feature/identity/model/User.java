package com.rin.mrlamel.feature.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.USER_ROLE;
import com.rin.mrlamel.common.constant.USER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
})
@ToString
public class User {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    String email;
    @JsonIgnore
    String password;
    @Column(nullable = false, length = 50)
    String fullName;
    @Column(nullable = false, length = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    String phoneNumber;
    LocalDate dob;
    boolean isActive = true; // Changed from Boolean to primitive boolean for better performance
    @Enumerated(EnumType.STRING)
    USER_STATUS status = USER_STATUS.OK; // Default status is OK
    @Enumerated(EnumType.STRING)
    USER_ROLE role = USER_ROLE.STUDENT; // Default role is STUDENT
    String avatarUrl;
    String address;
    @CreationTimestamp
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    UserCode userCode = new UserCode();
}
