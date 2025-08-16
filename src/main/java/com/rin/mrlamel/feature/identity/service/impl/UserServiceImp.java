package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.common.utils.OtpProvider;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
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

import java.time.LocalDateTime;
import java.util.*;

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
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
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
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public List<User> getAvailableTeachersForSessions(List<ClassSession> sessions) {
        if (sessions.isEmpty()) return Collections.emptyList();

        // Bắt đầu với session đầu tiên
        Set<User> available = new HashSet<>(userRepository.getAvailableTeachersForSession(
                sessions.get(0).getDate(),
                sessions.get(0).getStartTime(),
                sessions.get(0).getEndTime(),
                sessions.get(0).getId() != null ? sessions.get(0).getId() : 0L
        ));

        for (int i = 1; i < sessions.size(); i++) {
            if(available.isEmpty()) break; // Nếu không còn giáo viên nào rảnh thì dừng
            ClassSession session = sessions.get(i);
            List<User> freeForSession = userRepository.getAvailableTeachersForSession(
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getId() != null ? session.getId() : 0L
            );
            available.retainAll(freeForSession); // giữ giao cho tất cả session
        }

        return new ArrayList<>(available);
    }

    @Override
    public boolean isTeacherAvailableForAllSessions(Long teacherId, List<ClassSession> sessions) {
        for (ClassSession session : sessions) {
            long conflicts = userRepository.countConflictingSessions(
                    teacherId,
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getId() != null ? session.getId() : 0L
            );
            if (conflicts > 0) {
                return false; // Teacher bận
            }
        }
        return true; // Teacher rảnh
    }

    @Override
    public List<User> getAllTeachers() {
        return userRepository.findAllTeachers();
    }

    @Override
    public void assignTeacherToSessions(Long teacherId, List<ClassSession> classSessions) {
        // Kiểm tra xem teacherId có hợp lệ không
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));

        // Kiểm tra xem giáo viên có khả dụng cho tất cả session không
        if (!isTeacherAvailableForAllSessions(teacherId, classSessions)) {
            throw new IllegalArgumentException("Teacher with ID " + teacherId + " is not available for all provided sessions.");
        }
        LocalDateTime now = LocalDateTime.now();
        // Gán giáo viên cho từng session
        for (ClassSession session : classSessions) {
            if(session.getDate().isBefore(now.toLocalDate()) ||
               (session.getDate().isEqual(now.toLocalDate()) && session.getStartTime().isBefore(now.toLocalTime()))) {
                continue; // Bỏ qua các session đã qua
            }
            session.setTeacher(teacher);
        }

        teacher.getTeacherClassSessions().addAll(classSessions); // Cập nhật danh sách session của giáo viên
        // Lưu tất cả session đã cập nhật
        userRepository.save(teacher);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }



}