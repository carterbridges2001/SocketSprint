package com.socketsprint.service;

import com.socketsprint.dto.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<ChatMessage> sendMessage(ChatMessage message);
    Flux<ChatMessage> getMessageStream();
}
