package com.rin.mrlamel.feature.ai.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.common.utils.JwtTokenProvider;
import com.rin.mrlamel.feature.ai.dto.AskRequest;
import com.rin.mrlamel.feature.ai.dto.AskResponse;
import com.rin.mrlamel.feature.ai.service.AIService;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AIController {
    AIService aiService;
    UserService userService;
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/ask")
    public ApiRes<AskResponse> askAI(@RequestBody AskRequest askRequest,
                                     @RequestHeader("Authorization") String token){


        return ApiRes.success(AskResponse.builder()
                .answer(aiService.ask(askRequest.getQuestion(), token.substring(7)))
                .build());
    }


}
