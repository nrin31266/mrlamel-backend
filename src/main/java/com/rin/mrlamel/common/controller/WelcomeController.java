package com.rin.mrlamel.common.controller;

import com.rin.mrlamel.common.utils.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeController {
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/welcome")
    public String welcome(HttpServletResponse response) {
        Cookie cookie = new Cookie("mrToken", "123456abcdef"); // 👈 value có thể là JWT hoặc test string
        cookie.setHttpOnly(true); // bảo mật, JS không đọc được
        cookie.setSecure(false); // nếu bạn dùng HTTPS thì để true
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

        response.addCookie(cookie);
        return "Welcome to MrLamEl API";
    }

    @GetMapping("/api/welcome")
    public ResponseEntity<Object> apiWelcome(Authentication authentication) {
        Map<String, Object> userDetails = Map.of(
                "email", jwtTokenProvider.getSubject(authentication),
                "authorities", jwtTokenProvider.getClaim(authentication, "roles"),
                "id", jwtTokenProvider.getClaim(authentication, "id")
        );
        return ResponseEntity.ok(userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/welcome")
    public String adminWelcome() {
        return "Welcome to MrLamEl Admin API";
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/student/welcome")
    public String studentWelcome() {
        return "Welcome to MrLamEl Student API";
    }
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/teacher/welcome")
    public String teacherWelcome() {
        return "Welcome to MrLamEl Teacher API";
    }
}
