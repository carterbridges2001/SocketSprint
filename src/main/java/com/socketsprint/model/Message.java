package com.socketsprint.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {
    private String content;
    private String senderId;
    private String senderName;
    private String channelId;
    private MessageType type;
    private Map<String, Object> metadata;
    
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM
    }
}
