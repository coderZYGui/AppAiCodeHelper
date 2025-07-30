package com.sunny.appaicodehelper.controller;

import com.sunny.appaicodehelper.ai.AiCoderHelperService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */
@RestController
@RequestMapping("/ai")
public class AiCodeHelperController {

    @Resource
    private AiCoderHelperService aiCoderHelperService;

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(int memoryId, String message) {
        return aiCoderHelperService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}

