package com.example.fma_fe.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatMessage {
    private String messageId;
    private String senderId;
    private String senderName;
    private String message;
    private long   timestamp;
    private String messageType; // "text", "image"…

    public ChatMessage() {}

    public ChatMessage(String messageId, String senderId, String senderName,
                       String message, long timestamp, String messageType) {
        this.messageId   = messageId;
        this.senderId    = senderId;
        this.senderName  = senderName;
        this.message     = message;
        this.timestamp   = timestamp;
        this.messageType = messageType;
    }

    // Getters - Setters (tương tự)

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

