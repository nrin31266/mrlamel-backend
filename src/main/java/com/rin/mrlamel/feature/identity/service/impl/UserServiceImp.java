package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.common.utils.OtpProvider;
import com.rin.mrlamel.feature.email.service.EmailService;
import com.rin.mrlamel.feature.identity.dto.req.CreateUserRq;
import com.rin.mrlamel.feature.identity.dto.req.UpdateUserReq;
import com.rin.mrlamel.feature.identity.mapper.UserMapper;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImp implements com.rin.mrlamel.feature.identity.service.UserService {

   UserRepository userRepository;
   PageableMapper pageableMapper;
   UserMapper userMapper;
    EmailService emailProvider;
    OtpProvider otpProvider;
    PasswordEncoder passwordEncoder;

    @Override
    public PageableDto<User> getAllUsers(int page, int size, String sortBy, String sortDirection, String search, String role, String status) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                var loweredSearch = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), loweredSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), loweredSearch)
                ));
            }
            if (role != null && !role.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if(predicates.isEmpty()) {
                // If no filters are applied, return all users
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(page , size, sort);
        Page<User> pageResult = userRepository.findAll(spec, pageable);

        return  pageableMapper.toPageableDto(pageResult);
    }

    @Override
    public void createUser(CreateUserRq createUserRq) throws MessagingException {
        log.info("Is active: {}", createUserRq.isActive());
        if(userRepository.existsByEmail(createUserRq.getEmail())) {
            throw new RuntimeException("User with email " + createUserRq.getEmail() + " already exists");
        }
        User user = userMapper.toUser(createUserRq);

//        String password = otpProvider.generateOtp(10); // Generate a random password or OTP

        String password = "123"; // Default password, should be changed by the user later
        user.setPassword(passwordEncoder.encode(password)); // Encode the password
        if (user.isProfileComplete()) {
            user.setCompletedProfile(true); // Set completedProfile to true if all fields are filled
        }
        userRepository.save(user);
        log.info("User created: {}", user.getEmail());

        // Send welcome email
        emailProvider.sendEmail(
                List.of(user.getEmail()),
                "Welcome to MR Lamel",
                String.format("Hello %s,\n\nWelcome to MR Lamel! Your account has been created successfully.\n\nYour default password is: %s\nPlease change it after your first login.\n\nBest regards,\nMR Lamel Team", user.getFullName(), password)
        );
    }

    @Override
    public void updateUser(String userId, UpdateUserReq updateUserRq) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update user details
        userMapper.updateUser(updateUserRq, user);
        if (user.isProfileComplete()) {
            user.setCompletedProfile(true); // Set completedProfile to true if all fields are filled
        }

        // Save the updated user
        userRepository.save(user);
        log.info("User updated: {}", user.getEmail());

    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}