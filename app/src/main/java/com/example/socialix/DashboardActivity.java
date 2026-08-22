package com.example.socialix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Safely apply system bar insets
        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        View ivAiHeader = findViewById(R.id.ivAiHeader);
        View ivMessagesHeader = findViewById(R.id.ivMessagesHeader);
        View ivNotification = findViewById(R.id.ivNotification);
        View ivProfileHeader = findViewById(R.id.ivProfileHeader);

        if (ivAiHeader != null) {
            ivAiHeader.setOnClickListener(v -> startActivity(new Intent(this, AiAssistantActivity.class)));
        }

        if (ivMessagesHeader != null) {
            ivMessagesHeader.setOnClickListener(v -> startActivity(new Intent(this, MessagesActivity.class)));
        }

        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        }

        if (ivProfileHeader != null) {
            ivProfileHeader.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        // Bottom Navigation Views
        View tabHome = findViewById(R.id.tabHome);
        View tabCalendar = findViewById(R.id.tabCalendar);
        View tabCreatePost = findViewById(R.id.tabCreatePost);
        View tabAnalytics = findViewById(R.id.tabAnalytics);
        View tabProfile = findViewById(R.id.tabProfile);

        if (tabCreatePost != null) {
            tabCreatePost.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));
        }

        if (tabCalendar != null) {
            tabCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        }

        if (tabAnalytics != null) {
            tabAnalytics.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        }

        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
    }
}