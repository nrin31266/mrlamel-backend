package com.rin.mrlamel.feature.identity.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.identity.dto.req.LoginRq;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public ApiRes<AuthRes> login(@Validated @RequestBody LoginRq rq, HttpServletResponse response) {
        // Implement login logic here
        return ApiRes.<AuthRes>builder()
                .data(authenticationService.login(rq, response))
                .build();
    }

    @PostMapping("/auth/register")
    public ApiRes<AuthRes> register(@Validated @RequestBody RegisterReq rq, HttpServletResponse response) throws MessagingException {

        // Implement registration logic here
        return ApiRes.<AuthRes>builder()
                .data(authenticationService.register(rq, response))
                .build();
    }

    @PostMapping("/auth/refresh-token")
    public ApiRes<AuthRes> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        return ApiRes.<AuthRes>builder()
                .data(authenticationService.refreshToken(refreshToken, response))
                .build();
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        String errorMessage = "";
        try {
            authenticationService.logout(refreshToken, response);
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            // Don't return an error if logout fails, just log it
            errorMessage = e.getMessage();

        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully! " + (errorMessage.isEmpty() ? "" : "But: " + errorMessage)));
    }


}
