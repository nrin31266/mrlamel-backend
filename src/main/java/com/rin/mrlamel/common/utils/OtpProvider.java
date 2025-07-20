package com.rin.mrlamel.common.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OtpProvider {
    PasswordEncoder passwordEncoder;
    public String generateOtp(int optLength) {
        StringBuilder otp = new StringBuilder(optLength);
        for (int i = 0; i < optLength; i++) {
            int digit = (int) (Math.random() * 10);
            otp.append(digit);
        }
        return otp.toString();
    }

    public String encodeOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    public boolean verifyOtp(String rawOtp, String encodedOtp, LocalDateTime expirationTime) {
        if (LocalDateTime.now().isAfter(expirationTime)) {
            return false; // OTP has expired
        }
        return passwordEncoder.matches(rawOtp, encodedOtp);
    }
}
