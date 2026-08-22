package com.example.socialix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MessagesActivity extends AppCompatActivity {

    private TextView tabComments, tabDMs;
    private TextView chipAll, chipUnread, chipReplied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages);

        // System insets handling
        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        // --- Back Button Handler ---
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack == null) {
            btnBack = findViewById(R.id.btnBack);
        }
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Tabs & Filter Views
        tabComments = findViewById(R.id.tabComments);
        tabDMs = findViewById(R.id.tabDMs);
        chipAll = findViewById(R.id.chipAll);
        chipUnread = findViewById(R.id.chipUnread);
        chipReplied = findViewById(R.id.chipReplied);

        // Comments vs DMs Tab Toggle
        if (tabComments != null && tabDMs != null) {
            tabComments.setOnClickListener(v -> {
                tabComments.setBackgroundResource(R.drawable.bg_segmented_active);
                tabComments.setTextColor(0xFFFFFFFF);
                tabDMs.setBackgroundResource(android.R.color.transparent);
                tabDMs.setTextColor(0xFFB9B2C9);
            });

            tabDMs.setOnClickListener(v -> {
                tabDMs.setBackgroundResource(R.drawable.bg_segmented_active);
                tabDMs.setTextColor(0xFFFFFFFF);
                tabComments.setBackgroundResource(android.R.color.transparent);
                tabComments.setTextColor(0xFFB9B2C9);
            });
        }

        // Filter Chips Selector
        if (chipAll != null) chipAll.setOnClickListener(v -> selectChip(chipAll));
        if (chipUnread != null) chipUnread.setOnClickListener(v -> selectChip(chipUnread));
        if (chipReplied != null) chipReplied.setOnClickListener(v -> selectChip(chipReplied));

        // --- Bottom Navigation Handlers ---
        View tabHome = findViewById(R.id.tabHome);
        View tabCalendar = findViewById(R.id.tabCalendar);
        View tabCreatePost = findViewById(R.id.tabCreatePost);
        View tabAnalytics = findViewById(R.id.tabAnalytics);
        View tabProfile = findViewById(R.id.tabProfile);

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                startActivity(new Intent(MessagesActivity.this, DashboardActivity.class));
                finish();
            });
        }

        if (tabCalendar != null) {
            tabCalendar.setOnClickListener(v -> {
                startActivity(new Intent(MessagesActivity.this, CalendarActivity.class));
                finish();
            });
        }

        if (tabCreatePost != null) {
            tabCreatePost.setOnClickListener(v -> {
                startActivity(new Intent(MessagesActivity.this, CreatePostActivity.class));
            });
        }

        if (tabAnalytics != null) {
            tabAnalytics.setOnClickListener(v -> {
                startActivity(new Intent(MessagesActivity.this, AnalyticsActivity.class));
                finish();
            });
        }

        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> {
                startActivity(new Intent(MessagesActivity.this, ProfileActivity.class));
                finish();
            });
        }
    }

    private void selectChip(TextView selected) {
        TextView[] chips = {chipAll, chipUnread, chipReplied};
        for (TextView c : chips) {
            if (c != null) {
                c.setBackgroundResource(R.drawable.bg_filter_inactive);
                c.setTextColor(0xFFB9B2C9);
            }
        }
        if (selected != null) {
            selected.setBackgroundResource(R.drawable.bg_filter_active);
            selected.setTextColor(0xFFFFFFFF);
        }
    }
}