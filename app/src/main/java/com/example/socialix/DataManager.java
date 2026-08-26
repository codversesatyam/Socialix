package com.example.socialix;

import android.content.Context;

import com.example.socialix.models.ChatMessage;
import com.example.socialix.models.NotificationItem;
import com.example.socialix.models.PostModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class DataManager {
    private static DataManager instance;

    // Fast chronologically ordered post queue
    private final PriorityQueue<PostModel> scheduledPostQueue;
    private final List<PostModel> allPosts;

    // FIFO Queue for Chat Streaming & AI Prompts
    private final LinkedList<ChatMessage> chatHistory;

    // Fast indexed notification list
    private final List<NotificationItem> notificationList;

    private DataManager() {
        allPosts = new ArrayList<>();
        scheduledPostQueue = new PriorityQueue<>(
                (p1, p2) -> {
                    Long t1 = p1.getScheduledTimestamp() != null ? p1.getScheduledTimestamp() : 0L;
                    Long t2 = p2.getScheduledTimestamp() != null ? p2.getScheduledTimestamp() : 0L;
                    return Long.compare(t1, t2);
                }
        );
        chatHistory = new LinkedList<>();
        notificationList = new ArrayList<>();
        seedInitialData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void seedInitialData() {
        // Initial Posts
        addPost(new PostModel("1", "Super excited to announce our upcoming feature release! Stay tuned ",
                Arrays.asList("LinkedIn", "Instagram"), System.currentTimeMillis() + 3600000, "SCHEDULED"));
        addPost(new PostModel("2", "10 tips to optimize Android Studio memory and Gradle build speed ",
                Collections.singletonList("LinkedIn"), System.currentTimeMillis() + 86400000, "SCHEDULED"));

        // Initial Chat History
        chatHistory.add(new ChatMessage("Hello Satyam! I am your AI Social Assistant. Ask me to craft viral captions, generate hashtags, or optimize your posting times!", true));

        // Initial Notifications
        notificationList.add(new NotificationItem("n1", "Post Published Successfully", "Your scheduled post on LinkedIn is now live.", "10m ago", NotificationItem.Type.PUBLISHED));
        notificationList.add(new NotificationItem("n2", "Audience Growth Alert", "Your account reach jumped +32% this week!", "2h ago", NotificationItem.Type.GROWTH));
        notificationList.add(new NotificationItem("n3", "New Message from @alex.dev", "Hey Satyam! Are you free for a quick collab call?", "Yesterday", NotificationItem.Type.MESSAGE));
    }

    // --- Post Operations ---
    public void addPost(PostModel post) {
        allPosts.add(0, post);
        if ("SCHEDULED".equalsIgnoreCase(post.getStatus())) {
            scheduledPostQueue.offer(post);
        }
    }

    public List<PostModel> getAllPosts() { return new ArrayList<>(allPosts); }
    public PostModel getNextUpcomingPost() { return scheduledPostQueue.peek(); }

    // --- Chat Operations ---
    public void addChatMessage(ChatMessage msg) {
        chatHistory.add(msg);
        if (chatHistory.size() > 50) { // Sliding window cap to prevent memory bloat
            chatHistory.removeFirst();
        }
    }

    public List<ChatMessage> getChatHistory() { return chatHistory; }
    public void clearChatHistory() { chatHistory.clear(); }

    // --- Notification Operations ---
    public List<NotificationItem> getNotifications() { return notificationList; }

    public int getUnreadNotificationCount() {
        int count = 0;
        for (NotificationItem item : notificationList) {
            if (!item.isRead()) count++;
        }
        return count;
    }

    public void markAllNotificationsRead() {
        for (NotificationItem item : notificationList) {
            item.setRead(true);
        }
    }

    public interface CloudSyncCallBack{
        void onSyncComplete(boolean success);
    }
    public void syncFeedFromCloud(Context context, CloudSyncCallBack callback) {
        com.example.socialix.network.ApiClient.getApiService(context).getPosts().enqueue(new retrofit2.Callback<List<PostModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<PostModel>> call, retrofit2.Response<List<PostModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update local list
                    allPosts.clear();
                    allPosts.addAll(response.body());
                    if (callback != null) {
                        callback.onSyncComplete(true);
                    }
                } else {
                    if (callback != null) {
                        callback.onSyncComplete(false);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<PostModel>> call, Throwable t) {
                if (callback != null) {
                    callback.onSyncComplete(false);
                }
            }
        });
    }
}