package com.rin.mrlamel.feature.identity.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.identity.dto.req.LoginRq;
import com.rin.mrlamel.feature.identity.dto.req.RegisterReq;
import com.rin.mrlamel.feature.identity.dto.res.AuthRes;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .data(authenticationService.login(rq.getEmail(), rq.getPassword(), response))
                .build();
    }

    @PostMapping("/auth/register")
    public ApiRes<AuthRes> register(@Validated @RequestBody RegisterReq rq, HttpServletResponse response) {

        // Implement registration logic here
        return ApiRes.<AuthRes>builder()
                .data(authenticationService.register(rq, response))
                .build();
    }
}
