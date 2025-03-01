package com.socketsprint.controller;

import com.socketsprint.dto.ChatMessage;
import com.socketsprint.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public Mono<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        return chatService.sendMessage(message);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> streamMessages() {
        return chatService.getMessageStream();
    }
}
