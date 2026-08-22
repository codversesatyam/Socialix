package com.example.socialix.models;

public class NotificationItem {
    public enum Type { PUBLISHED, GROWTH, MESSAGE }

    private String id;
    private String title;
    private String description;
    private String timeAgo;
    private Type type;
    private boolean isRead;

    public NotificationItem(String id, String title, String description, String timeAgo, Type type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeAgo = timeAgo;
        this.type = type;
        this.isRead = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTimeAgo() { return timeAgo; }
    public Type getType() { return type; }
    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { this.isRead = read; }
}