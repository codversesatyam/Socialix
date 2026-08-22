package com.example.socialix.models;

import java.io.Serializable;
import java.util.List;

public class PostModel implements Serializable, Comparable<PostModel> {
    private String id;
    private String content;
    private List<String> platforms;
    private long scheduledTimestamp;
    private String status;

    public PostModel(String id, String content, List<String> platforms, long scheduledTimestamp, String status) {
        this.id = id;
        this.content = content;
        this.platforms = platforms;
        this.scheduledTimestamp = scheduledTimestamp;
        this.status = status;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public List<String> getPlatforms() { return platforms; }
    public long getScheduledTimestamp() { return scheduledTimestamp; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public int compareTo(PostModel other) {
        return Long.compare(this.scheduledTimestamp, other.scheduledTimestamp);
    }
}