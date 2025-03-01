package com.socketsprint.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class ChatMessage {
    private String id;
    private String content;
    private String senderId;
    private String senderName;
    private String channelId;
    private MessageType type;
    private Instant timestamp;

    public enum MessageType {
        CHAT, JOIN, LEAVE, ERROR
    }

    @JsonCreator
    public ChatMessage(
            @JsonProperty("content") String content,
            @JsonProperty("senderId") String senderId,
            @JsonProperty("senderName") String senderName,
            @JsonProperty("channelId") String channelId,
            @JsonProperty("type") MessageType type) {
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.channelId = channelId;
        this.type = type;
        this.timestamp = Instant.now();
    }
}
