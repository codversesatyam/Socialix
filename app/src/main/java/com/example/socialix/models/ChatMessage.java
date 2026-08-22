package com.example.socialix.models;

public class ChatMessage {
    private String message;
    private boolean isAi;
    private long timestamp;

    public ChatMessage(String message, boolean isAi) {
        this.message = message;
        this.isAi = isAi;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() { return message; }
    public boolean isAi() { return isAi; }
    public long getTimestamp() { return timestamp; }
}