package com.socketsprint.service.impl;

import com.socketsprint.dto.ChatMessage;
import com.socketsprint.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final Sinks.Many<ChatMessage> messageSink;
    private final Flux<ChatMessage> messageFlux;

    public ChatServiceImpl() {
        this.messageSink = Sinks.many().multicast().onBackpressureBuffer();
        this.messageFlux = this.messageSink.asFlux().cache(0);
    }

    @Override
    public Mono<ChatMessage> sendMessage(ChatMessage message) {
        log.info("Sending message: {}", message);
        messageSink.tryEmitNext(message);
        return Mono.just(message);
    }

    @Override
    public Flux<ChatMessage> getMessageStream() {
        return messageFlux;
    }
}
