package com.socketsprint.service;

import com.socketsprint.model.Message;
import com.socketsprint.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Service for handling message persistence operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePersistenceService {

    private final MessageRepository messageRepository;
    
    /**
     * Save a new message to the database
     */
    public Mono<Message> saveMessage(Message message) {
        if (message == null) {
            return Mono.error(new IllegalArgumentException("Message cannot be null"));
        }
        
        // Set expiration time if not already set
        if (message.getExpireAt() == null) {
            message.setExpireAt(Instant.now().plus(Duration.ofDays(90))); // Default 90 days TTL
        }
        
        return messageRepository.save(message)
                .doOnSuccess(msg -> log.debug("Message saved: {}", msg.getId()));
    }
    
    /**
     * Get recent messages for a channel with pagination
     */
    public Flux<Message> getRecentMessages(String channelId, int limit) {
        return messageRepository.findByChannelIdOrderByCreatedAtDesc(
            channelId,
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }
    
    /**
     * Get messages in a channel after a specific message ID
     */
    public Flux<Message> getMessagesAfter(String channelId, String lastMessageId) {
        return messageRepository.findByChannelIdAndIdAfterOrderByCreatedAtAsc(channelId, lastMessageId);
    }
    
    /**
     * Mark messages as read by a user
     */
    public Mono<Void> markMessagesAsRead(String channelId, String userId) {
        return messageRepository.findUnreadMessages(channelId, userId)
                .flatMap(message -> {
                    message.getReadBy().add(userId);
                    return messageRepository.save(message);
                })
                .then();
    }
    
    /**
     * Get unread message count for a user in a channel
     */
    public Mono<Long> getUnreadCount(String channelId, String userId) {
        return messageRepository.countUnreadMessages(channelId, userId);
    }
    
    /**
     * Get messages in a time range for a channel
     */
    public Flux<Message> getMessagesInTimeRange(String channelId, Instant start, Instant end) {
        return messageRepository.findByChannelIdAndCreatedAtBetween(channelId, start, end);
    }
}
