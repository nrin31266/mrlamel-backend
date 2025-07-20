package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.constant.USER_STATUS;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.common.utils.OtpProvider;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.mapper.UserMapper;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.repository.UserRepository;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import jakarta.servlet.http.Cookie;       // Đối tượng Cookie
import jakarta.servlet.http.HttpServletResponse; // Để thêm cookie vào response
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    OtpProvider otpProvider;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    JwtTokenProvider jwtTokenProvider;
    UserMapper userMapper;

    private void saveRefreshToken(String refreshToken, HttpServletResponse response) {
        // Implementation for saving the refresh token
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // Chỉ cho phép truy cập từ phía máy chủ
        cookie.setSecure(false); // Chỉ gửi cookie qua HTTPS
        cookie.setPath("/"); // Cookie có hiệu lực trên đường dẫn này
        cookie.setMaxAge(60 * 60 * 24 * 7); // Cookie có hiệu lực trong 7 ngày
        response.addCookie(cookie); // Thêm cookie vào phản hồi HTTP
    }

    @Override
    public AuthRes login(String email, String password, HttpServletResponse response) {
        User user = findUserByEmail(email);
        if (!verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid password for user: " + email);
        }
        // Generate JWT token and refresh token
        return getAuthRes(user, response);
    }

    @Override
    public AuthRes register(RegisterReq rq, HttpServletResponse response) {
        if (userRepository.existsByEmail(rq.getEmail())) {
            throw new RuntimeException("User already exists with email: " + rq.getEmail());
        }
        User user = userMapper.toUser(rq);
        user.setPassword(passwordEncoder.encode(rq.getPassword()));
        user.setStatus(USER_STATUS.OK);
        userRepository.save(user);
        // Generate JWT token and refresh token
        return getAuthRes(user, response);
    }

    private AuthRes getAuthRes(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.generateToken(user, 5 * 60); // 5 minutes
        String refreshToken = jwtTokenProvider.generateToken(user, 7 * 24 * 60 * 60); // 7 days
        saveRefreshToken(refreshToken, response); // Save refresh token in cookie
        return AuthRes.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
