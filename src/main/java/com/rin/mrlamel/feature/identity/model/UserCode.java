package com.rin.mrlamel.feature.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserCode {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // Reference to the user this code belongs to
    @JsonIgnore
    String resetPasswordToken; // Token for password reset
    @JsonIgnore
    Date resetPasswordExpiresAt; // Expiration time for the password reset token
    @JsonIgnore
    String emailVerificationToken; // Code for email verification
    @JsonIgnore
    Date emailVerificationExpiresAt; // Expiration time for the email verification code
}
