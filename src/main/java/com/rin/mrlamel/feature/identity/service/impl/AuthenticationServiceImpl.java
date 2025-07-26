package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.constant.USER_STATUS;
import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.feature.email.service.EmailService;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.common.utils.OtpProvider;
import com.rin.mrlamel.feature.identity.dto.req.LoginRq;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.mapper.UserMapper;

import com.rin.mrlamel.feature.identity.model.RefreshToken;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.model.UserCode;
import com.rin.mrlamel.feature.identity.repository.RefreshTokenRepository;
import com.rin.mrlamel.feature.identity.repository.UserRepository;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import jakarta.servlet.http.Cookie;       // Đối tượng Cookie
import jakarta.servlet.http.HttpServletResponse; // Để thêm cookie vào response
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    EmailService emailProvider;
    OtpProvider otpProvider;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    JwtTokenProvider jwtTokenProvider;
    UserMapper userMapper;
    int durationInMinutes = 1;
    long accessTokenDuration = 1 * 60 * 1000; // 5 minutes
    long refreshTokenDuration = 7 * 24 * 60* 60 * 1000; // 7 days
    RefreshTokenRepository refreshTokenRepository;


    private void saveRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
        // Implementation for saving the refresh token
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // Chỉ cho phép truy cập từ phía máy chủ
        cookie.setSecure(false); // Chỉ gửi cookie qua HTTPS
        cookie.setPath("/"); // Cookie có hiệu lực trên đường dẫn này
        cookie.setMaxAge(60 * 60 * 24 * 7); // Cookie có hiệu lực trong 7 ngày
        response.addCookie(cookie); // Thêm cookie vào phản hồi HTTP
    }

    @Override
    public AuthRes login(LoginRq req, HttpServletResponse response) {
        User user = findUserByEmail(req.getEmail());
        if (!verifyPassword(req.getPassword(), user.getPassword())) {
            throw new AppException("Invalid password for user: " + req.getEmail());
        }
        // Generate JWT token and refresh token
        return getAuthRes(user, response, req.getDeviceId(), req.getDeviceName());
    }

    @Override
    public AuthRes register(RegisterReq rq, HttpServletResponse response) throws MessagingException {
        if (userRepository.existsByEmail(rq.getEmail())) {
            throw new AppException("User already exists with email: " + rq.getEmail());
        }
        User user = userMapper.toUser(rq);
        user.setPassword(passwordEncoder.encode(rq.getPassword()));
        user.setStatus(USER_STATUS.OK);
        UserCode userCode = new UserCode();
        userCode.setUser(user);
        String otp = otpProvider.generateOtp(6);
        userCode.setEmailVerificationToken(passwordEncoder.encode(otp));
        userCode.setEmailVerificationExpiresAt(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli())); // 1 minute from now
        user.setUserCode(userCode);
        userRepository.save(user);
        // Generate JWT token and refresh token
        emailProvider.sendEmail(
                List.of(user.getEmail()),
                "Welcome to MrLamEl",
                "<h1>Welcome to MrLamEl</h1>" +
                        "<p>Your verification code is: <strong>" + otp + "</strong></p>" +
                        "<p>Please use this code to verify your email address.</p>"
        );
        return getAuthRes(user, response, rq.getDeviceId(), rq.getDeviceName());
    }

    @Override
    public void sendEmailVerification(Authentication authentication) throws MessagingException {
        User user = findUserByEmail(jwtTokenProvider.getSubject(authentication));
        if(user.isActive()) {
            throw new AppException("Email already verified for user: " + user.getEmail());
        }
        UserCode userCode = user.getUserCode();
        if(userCode ==null){
            userCode = new UserCode();
            userCode.setUser(user);
        }
        String otp = otpProvider.generateOtp(6);
        userCode.setEmailVerificationToken(passwordEncoder.encode(otp));
        userCode.setEmailVerificationExpiresAt(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli())); // 1 minute from now
        user.setUserCode(userCode);
        userRepository.save(user);
        emailProvider.sendEmail(
                List.of(user.getEmail()),
                "Email Verification",
                "<h1>Email Verification</h1>" +
                        "<p>Your verification code is: <strong>" + otp + "</strong></p>" +
                        "<p>Please use this code to verify your email address.</p>"
        );
    }

    @Override
    public void sendResetPassword(String email) throws MessagingException {
        User user = findUserByEmail(email);
        UserCode userCode = user.getUserCode();
        if (userCode == null) {
            userCode = new UserCode();
            userCode.setUser(user);
        }
        String otp = otpProvider.generateOtp(6);
        userCode.setResetPasswordToken(passwordEncoder.encode(otp));
        userCode.setResetPasswordExpiresAt(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli())); // 1 minute from now
        user.setUserCode(userCode);
        userRepository.save(user);
        emailProvider.sendEmail(
                List.of(user.getEmail()),
                "Password Reset",
                "<h1>Password Reset</h1>" +
                        "<p>Your password reset code is: <strong>" + otp + "</strong></p>" +
                        "<p>Please use this code to reset your password.</p>"
        );
    }

    @Override
    public void verifyEmail(Authentication authentication, String token) {
        User user = findUserByEmail(jwtTokenProvider.getSubject(authentication));
        UserCode userCode = user.getUserCode();
        if (userCode == null || userCode.getEmailVerificationToken() == null) {
            throw new AppException("No verification token found for user: " + user.getEmail());
        }
        if (!passwordEncoder.matches(token, userCode.getEmailVerificationToken())) {
            throw new AppException("Invalid verification token for user: " + user.getEmail());
        }
        if (userCode.getEmailVerificationExpiresAt().before(new Date())) {
            throw new AppException("Verification token expired for user: " + user.getEmail());
        }
        // Mark email as verified
        user.setActive(true);
        user.getUserCode().setEmailVerificationToken(null); // Clear the token after verification
        user.getUserCode().setEmailVerificationExpiresAt(null); // Clear the expiration date
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String email, String token, String newPassword) {
        User user = findUserByEmail(email);
        UserCode userCode = user.getUserCode();
        if (userCode == null || userCode.getResetPasswordToken() == null) {
            throw new AppException("No password reset token found for user: " + email);
        }
        if (!passwordEncoder.matches(token, userCode.getResetPasswordToken())) {
            throw new AppException("Invalid password reset token for user: " + email);
        }
        if (userCode.getResetPasswordExpiresAt().before(new Date())) {
            throw new AppException("Password reset token expired for user: " + email);
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException("New password cannot be the same as the old password for user: " + email);
        }
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.getUserCode().setResetPasswordToken(null); // Clear the token after reset
        user.getUserCode().setResetPasswordExpiresAt(null); // Clear the expiration date
        userRepository.save(user);
    }

    @Override
    public AuthRes refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException("Refresh token is missing");
        }
        RefreshToken oRefreshToken = jwtTokenProvider.validateRefreshToken(refreshToken);

        Long userId = (Long) jwtTokenProvider.getClaim(refreshToken, "id");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found with ID: " + userId));
        return getAuthRes(user, response, oRefreshToken);
    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException("Refresh token is missing");
        }
        RefreshToken oRefreshToken = jwtTokenProvider.validateRefreshToken(refreshToken);
        // Revoke the refresh token
        refreshTokenRepository.delete(oRefreshToken);

        // Clear the cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0); // Set cookie to expire immediately
        cookie.setPath("/"); // Ensure the path matches where the cookie was set
        cookie.setHttpOnly(true); // Make the cookie HTTP only
        cookie.setSecure(false); // Set to true if using HTTPS
        response.addCookie(cookie);
    }

    @Override
    public User getUserByAuthentication(Authentication authentication) {
        return userRepository.findByEmail(jwtTokenProvider.getSubject(authentication)).orElse(null);
    }

    private AuthRes getAuthRes(User user, HttpServletResponse response, RefreshToken oRefreshToken) {
        String accessToken = jwtTokenProvider.generateToken(user, accessTokenDuration, "access_token"); // 5 minutes
        String refreshToken = jwtTokenProvider.generateToken(user, refreshTokenDuration, "refresh_token"); // 7 days
        oRefreshToken.setJti(jwtTokenProvider.getJti(refreshToken));
        oRefreshToken.setExpiryDate(new Date(Instant.now().plus(refreshTokenDuration, ChronoUnit.MILLIS).toEpochMilli()));
        oRefreshToken.setRevoked(false);
        refreshTokenRepository.save(oRefreshToken); // Update the existing refresh token
        saveRefreshTokenInCookie(refreshToken, response); // Save refresh token in cookie
        return AuthRes.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    private AuthRes getAuthRes(User user, HttpServletResponse response, String deviceId, String deviceName) {

        String accessToken = jwtTokenProvider.generateToken(user, accessTokenDuration, "access_token"); // 5 minutes
        String refreshToken = jwtTokenProvider.generateToken(user, refreshTokenDuration, "refresh_token"); // 7 days

        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId).ifPresentOrElse(
                existingToken -> {
                    // Update the existing refresh token
                    existingToken.setJti(jwtTokenProvider.getJti(refreshToken));
                    existingToken.setExpiryDate(new Date(Instant.now().plus(refreshTokenDuration, ChronoUnit.MILLIS).toEpochMilli()));
                    existingToken.setRevoked(false);
                    existingToken.setDeviceName(deviceName != null ? deviceName : "Unknown Device");
                    existingToken.setDeviceId(deviceId);
                    refreshTokenRepository.save(existingToken);
                },
                () -> {
                    // Create a new refresh token if it doesn't exist
                    refreshTokenRepository.save(new RefreshToken(null ,jwtTokenProvider.getJti(refreshToken),
                            new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()), false, user,
                            deviceId, deviceName != null ? deviceName : "Unknown Device"));
                }
        );


        saveRefreshTokenInCookie(refreshToken, response); // Save refresh token in cookie
        return AuthRes.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found with email: " + email));
    }

    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
