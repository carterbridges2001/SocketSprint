package com.socketsprint.model;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a chat message in the system.
 * Messages are stored in MongoDB with TTL index for automatic expiration.
 */
@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {
    
    @Field("content")
    private String content;

    @Indexed
    @Field("sender_id")
    private String senderId;

    @Field("sender_name")
    private String senderName;

    @Indexed
    @Field("channel_id")
    private String channelId;

    @Field("message_type")
    private MessageType type;

    @Field("read_by")
    private Set<String> readBy = new HashSet<>();

    @Field("metadata")
    private Map<String, Object> metadata;

    @Indexed(expireAfterSeconds = 0)
    @Field("expire_at")
    private Instant expireAt;
    
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM
    }
}
