package com.rin.mrlamel.feature.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rin.mrlamel.common.constant.GENDER;
import com.rin.mrlamel.common.constant.USER_ROLE;
import com.rin.mrlamel.common.constant.USER_STATUS;
import com.rin.mrlamel.feature.classroom.model.ClassEnrollment;
import com.rin.mrlamel.feature.classroom.model.ClassSchedule;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, length = 100)
    String email;
    @JsonIgnore
    String password;
    @Column(length = 50)
    String fullName;
    @Column(length = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    String phoneNumber;
    LocalDate dob;
    boolean isActive = false;// Changed from Boolean to primitive boolean for better performance
    @Enumerated(EnumType.STRING)
    USER_STATUS status = USER_STATUS.OK; // Default status is OK
    @Enumerated(EnumType.STRING)
    USER_ROLE role = USER_ROLE.STUDENT; // Default role is STUDENT
    String avatarUrl;
    String address;
    boolean completedProfile = false; // Indicates if the user has completed their profile
    @Enumerated(EnumType.STRING)
    GENDER gender;
    @CreationTimestamp
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    UserCode userCode = new UserCode();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RefreshToken> refreshTokens;
    //    @JsonIgnore
//    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.ALL)
//    List<ClassSchedule> teacherClassSchedules;
    @JsonIgnore
    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.ALL)
    List<ClassSession> teacherClassSessions;

    @JsonIgnore
    @OneToMany(mappedBy = "attendee", orphanRemoval = true, cascade = CascadeType.ALL)
    List<ClassEnrollment> classEnrollments; // List of class enrollments for the user

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    List<Clazz> createdByClasses;

    @JsonIgnore
    @ManyToMany(mappedBy = "managers")
    List<Clazz> managedClasses; // Classes managed by the user

    @ManyToMany
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public boolean isProfileComplete() {
        return fullName != null && !fullName.isEmpty() &&
               phoneNumber != null && !phoneNumber.isEmpty() &&
               dob != null &&
               address != null && !address.isEmpty() &&
               gender != null;
    }

}
