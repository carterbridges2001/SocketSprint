package com.socketsprint.repository;

import com.socketsprint.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findByChannelIdOrderByCreatedAtAsc(String channelId);
    
    Flux<Message> findByChannelIdAndIdAfterOrderByCreatedAtAsc(String channelId, String lastMessageId);
}
