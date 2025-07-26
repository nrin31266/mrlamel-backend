package com.rin.mrlamel.feature.identity.service;

import com.rin.mrlamel.feature.identity.dto.req.LoginRq;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    AuthRes login(LoginRq req, HttpServletResponse response);
    AuthRes register(RegisterReq rq, HttpServletResponse response) throws MessagingException;
    void sendEmailVerification(Authentication authentication) throws MessagingException;
    void sendResetPassword(String email) throws MessagingException;
    void verifyEmail(Authentication authentication, String token);
    void resetPassword(String email,String token, String newPassword);
    AuthRes refreshToken(String refreshToken, HttpServletResponse response);
    void logout(String refreshToken, HttpServletResponse response);
    User getUserByAuthentication(Authentication authentication);
}
