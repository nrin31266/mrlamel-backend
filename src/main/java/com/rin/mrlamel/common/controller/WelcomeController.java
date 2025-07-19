package com.rin.mrlamel.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to MrLamEl API";
    }
    @GetMapping("/api/welcome")
    public String apiWelcome() {
        return "Welcome to MrLamEl Security API";
    }
}
