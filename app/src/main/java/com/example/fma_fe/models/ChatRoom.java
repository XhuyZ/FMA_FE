package com.example.fma_fe.models;

import java.util.Map;

public class ChatRoom {
    private String roomId;
    private Map<String, Boolean> participants;
    private String lastMessage;
    private long   lastMessageTime;
    private String roomType; // "customer_support"

    public ChatRoom() {}

    public ChatRoom(String roomId, Map<String, Boolean> participants,
                    String lastMessage, long lastMessageTime, String roomType) {
        this.roomId         = roomId;
        this.participants   = participants;
        this.lastMessage    = lastMessage;
        this.lastMessageTime= lastMessageTime;
        this.roomType       = roomType;
    }

    // Getters - Setters

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}

