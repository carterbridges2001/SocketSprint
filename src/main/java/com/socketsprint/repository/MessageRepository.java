package com.socketsprint.repository;

import com.socketsprint.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Reactive MongoDB repository for message persistence operations.
 * Provides methods for querying messages with reactive streams.
 */
@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    /**
     * Find messages by channel ID, ordered by creation time (ascending)
     */
    Flux<Message> findByChannelIdOrderByCreatedAtAsc(String channelId);

    /**
     * Find messages by channel ID that were created after the specified message ID
     */
    Flux<Message> findByChannelIdAndIdAfterOrderByCreatedAtAsc(String channelId, String lastMessageId);
    
    /**
     * Find the most recent messages for a channel with pagination
     */
    Flux<Message> findByChannelIdOrderByCreatedAtDesc(String channelId, Pageable pageable);
    
    /**
     * Count unread messages for a user in a specific channel
     */
    @Query("{ 'channelId': ?0, 'readBy': { '$ne': ?1 } }")
    Mono<Long> countUnreadMessages(String channelId, String userId);
    
    /**
     * Find messages in a channel within a time range
     */
    Flux<Message> findByChannelIdAndCreatedAtBetween(
        String channelId, 
        Instant startDate, 
        Instant endDate
    );
    
    /**
     * Mark messages as read by a user
     */
    @Query("{ 'channelId': ?0, 'senderId': { '$ne': ?1 }, 'readBy': { '$ne': ?1 } }")
    Flux<Message> findUnreadMessages(String channelId, String userId);
}
