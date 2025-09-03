package com.rin.mrlamel.feature.ai.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.ai.dto.AskRequest;
import com.rin.mrlamel.feature.ai.dto.AskResponse;
import com.rin.mrlamel.feature.ai.service.DeepSeekAILocalService;
import com.rin.mrlamel.feature.ai.service.LlamaAIService;
import com.rin.mrlamel.feature.ai.service.DeepSeekAIService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AIController {
    LlamaAIService aiService;
    DeepSeekAIService deepSeekAIService;
    DeepSeekAILocalService deepSeekAILocalService;
    @PostMapping("/ask")
    public ApiRes<AskResponse> askAI(@RequestBody AskRequest askRequest,
                                     @RequestHeader("Authorization") String token){


        return ApiRes.success(AskResponse.builder()
                .answer(aiService.ask(askRequest.getQuestion(), token.substring(7)))
                .build());
    }

    @PostMapping("/deep-seek/ask")
    public ApiRes<AskResponse> askDeepSeekAI(@RequestBody AskRequest askRequest,
                                     @RequestHeader("Authorization") String token){


        return ApiRes.success(AskResponse.builder()
                .answer(deepSeekAIService.ask(askRequest.getQuestion(), token.substring(7)))
                .build());
    }

    // Thêm endpoint mới với RequestBody cho streaming
    @PostMapping(value = "/ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> askQuestionStream(
            @RequestBody AskRequest askRequest,
            @RequestHeader("Authorization") String token) {

        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return deepSeekAILocalService.ask(askRequest.getQuestion(), cleanToken)
                .map(chunk -> ServerSentEvent.builder(chunk).build());
    }



}
