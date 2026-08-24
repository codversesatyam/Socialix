package com.example.socialix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.socialix.DataManager;
import com.example.socialix.models.PostModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalPostsStat;
    private TextView tvScheduledPostsStat;
    private TextView tvUpcomingPostContent;
    private TextView tvUpcomingPostDate;
    private TextView tvPostIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind Dynamic Stats and Upcoming Post Card Views
        tvTotalPostsStat = findViewById(R.id.tvTotalPostsStat);
        tvScheduledPostsStat = findViewById(R.id.tvScheduledPostsStat);
        tvUpcomingPostContent = findViewById(R.id.tvUpcomingPostContent);
        tvUpcomingPostDate = findViewById(R.id.tvUpcomingPostDate);
        tvPostIcon = findViewById(R.id.tvPostIcon);

        // Header Actions
        ImageView ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        }

        ImageView ivMessagesHeader = findViewById(R.id.ivMessagesHeader);
        if (ivMessagesHeader != null) {
            ivMessagesHeader.setOnClickListener(v -> startActivity(new Intent(this, MessagesActivity.class)));
        }

        TextView ivAiHeader = findViewById(R.id.ivAiHeader);
        if (ivAiHeader != null) {
            ivAiHeader.setOnClickListener(v -> startActivity(new Intent(this, AiAssistantActivity.class)));
        }

        TextView ivProfileHeader = findViewById(R.id.ivProfileHeader);
        if (ivProfileHeader != null) {
            ivProfileHeader.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        // Bottom Navigation Bar
        View tabCreatePost = findViewById(R.id.tabCreatePost);
        if (tabCreatePost != null) {
            tabCreatePost.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));
        }

        View tabCalendar = findViewById(R.id.tabCalendar);
        if (tabCalendar != null) {
            tabCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        }

        View tabAnalytics = findViewById(R.id.tabAnalytics);
        if (tabAnalytics != null) {
            tabAnalytics.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        }

        View tabProfile = findViewById(R.id.tabProfile);
        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 1. Initial UI update from current in-memory cache
        renderDashboardData();

        // 2. Refresh live from PostgreSQL backend
        DataManager.getInstance().syncFeedFromCloud(success -> {
            runOnUiThread(() -> {
                if (success) {
                    renderDashboardData();
                }
            });
        });
    }

    private void renderDashboardData() {
        List<PostModel> allPosts = DataManager.getInstance().getAllPosts();

        // Count total and scheduled
        int totalCount = allPosts.size();
        int scheduledCount = 0;
        for (PostModel p : allPosts) {
            if ("SCHEDULED".equalsIgnoreCase(p.getStatus())) {
                scheduledCount++;
            }
        }

        if (tvTotalPostsStat != null) {
            tvTotalPostsStat.setText(String.valueOf(totalCount));
        }
        if (tvScheduledPostsStat != null) {
            tvScheduledPostsStat.setText(String.valueOf(scheduledCount));
        }

        // Upcoming Post Card
        PostModel nextPost = DataManager.getInstance().getNextUpcomingPost();
        if (nextPost != null) {
            if (tvUpcomingPostContent != null) {
                tvUpcomingPostContent.setText(nextPost.getContent());
            }

            if (tvUpcomingPostDate != null && nextPost.getScheduledTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
                tvUpcomingPostDate.setText(sdf.format(new Date(nextPost.getScheduledTimestamp())));
            }

            if (tvPostIcon != null && nextPost.getPlatforms() != null && !nextPost.getPlatforms().isEmpty()) {
                String firstPlatform = nextPost.getPlatforms().get(0).toLowerCase();
                if (firstPlatform.contains("linkedin")) {
                    tvPostIcon.setText("💼");
                } else if (firstPlatform.contains("twitter") || firstPlatform.contains("x")) {
                    tvPostIcon.setText("𝕏");
                } else if (firstPlatform.contains("tiktok")) {
                    tvPostIcon.setText("♪");
                } else {
                    tvPostIcon.setText("📷");
                }
            }
        } else {
            if (tvUpcomingPostContent != null) {
                tvUpcomingPostContent.setText("No upcoming posts scheduled");
            }
            if (tvUpcomingPostDate != null) {
                tvUpcomingPostDate.setText("Tap + below to create one");
            }
        }
    }
}




