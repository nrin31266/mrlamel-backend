package com.rin.mrlamel.feature.ai.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.ai.dto.AskRequest;
import com.rin.mrlamel.feature.ai.dto.AskResponse;
import com.rin.mrlamel.feature.ai.service.LlamaAIService;
import com.rin.mrlamel.feature.ai.service.DeepSeekAIService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AIController {
    LlamaAIService aiService;
    DeepSeekAIService deepSeekAIService;

    @GetMapping("/ask")
    public ApiRes<AskResponse> askAI(@RequestBody AskRequest askRequest,
                                     @RequestHeader("Authorization") String token){


        return ApiRes.success(AskResponse.builder()
                .answer(aiService.ask(askRequest.getQuestion(), token.substring(7)))
                .build());
    }

    @GetMapping("/deep-seek/ask")
    public ApiRes<AskResponse> askDeepSeekAI(@RequestBody AskRequest askRequest,
                                     @RequestHeader("Authorization") String token){


        return ApiRes.success(AskResponse.builder()
                .answer(deepSeekAIService.ask(askRequest.getQuestion(), token.substring(7)))
                .build());
    }


}
