package com.example.socialix.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PostModel implements Serializable, Comparable<PostModel> {

    @SerializedName("id")
    private String id;
    @SerializedName("content")
    private String content;
    @SerializedName("platform")
    private List<String> platforms;
    @SerializedName("scheduledTimestamp")
    private Long scheduledTimestamp;
    @SerializedName("status")
    private String status;

    public PostModel() {
    }

    public PostModel(String id, String content, List<String> platforms, Long scheduledTimestamp, String status) {
        this.id = id;
        this.content = content;
        this.platforms = platforms;
        this.scheduledTimestamp = scheduledTimestamp;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getPlatforms() {
        return platforms; }
    public void setPlatforms(List<String> platforms) { this.platforms = platforms; }

    public Long getScheduledTimestamp() { return scheduledTimestamp; }
    public void setScheduledTimestamp(Long scheduledTimestamp) { this.scheduledTimestamp = scheduledTimestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public int compareTo(PostModel other) {
        if (this.scheduledTimestamp == null && other.scheduledTimestamp == null) return 0;
        if (this.scheduledTimestamp == null) return 1;
        if (other.scheduledTimestamp == null) return -1;
        return Long.compare(this.scheduledTimestamp, other.scheduledTimestamp);
    }

    public boolean isPublished() {
        return "PUBLISHED".equalsIgnoreCase(this.status);
    }

    public boolean isScheduled() {
        return "SCHEDULED".equalsIgnoreCase(this.status);
    }

    public String getPlatform() {
        if (platforms != null && !platforms.isEmpty()) {
            return platforms.get(0);
        }
        return "General";
    }


}