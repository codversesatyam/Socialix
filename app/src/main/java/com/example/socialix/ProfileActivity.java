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

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // System insets handling
        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        // --- Top Bar Actions ---
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));
        }

        // --- Edit Profile Action ---
        View btnEditProfile = findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> Toast.makeText(this, "Profile edit dialog coming next", Toast.LENGTH_SHORT).show());
        }

        // --- Bottom Navigation Handlers ---
        View tabHome = findViewById(R.id.tabHome);
        View tabCalendar = findViewById(R.id.tabCalendar);
        View tabCreatePost = findViewById(R.id.tabCreatePost);
        View tabAnalytics = findViewById(R.id.tabAnalytics);

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
            });
        }

        if (tabCalendar != null) {
            tabCalendar.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, CalendarActivity.class));
                finish();
            });
        }

        if (tabCreatePost != null) {
            tabCreatePost.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, CreatePostActivity.class));
                finish();
            });
        }

        if (tabAnalytics != null) {
            tabAnalytics.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, AnalyticsActivity.class));
                finish();
            });
        }
    }
}