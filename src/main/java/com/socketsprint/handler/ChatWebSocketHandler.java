package com.socketsprint.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socketsprint.dto.ChatMessage;
import com.socketsprint.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> processMessage(session, message))
                .doOnError(error -> log.error("WebSocket error: {}", error.getMessage(), error))
                .onErrorResume(e -> Mono.empty())
                .then();
    }

    private Mono<Void> processMessage(WebSocketSession session, String message) {
        if (message == null) {
            return Mono.empty();
        }
        
        try {
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            return chatService.sendMessage(chatMessage)
                    .flatMap(msg -> {
                        try {
                            String jsonMessage = objectMapper.writeValueAsString(msg);
                            WebSocketMessage wsMessage = session.textMessage(jsonMessage);
                            return session.send(Mono.just(wsMessage));
                        } catch (JsonProcessingException e) {
                            log.error("Error serializing message: {}", e.getMessage());
                            return Mono.error(e);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error processing message: {}", e.getMessage());
            return Mono.error(new RuntimeException("Invalid message format"));
        }
    }
}
