package com.example.socialix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView filter7D, filter30D, filter90D, filter1Y;
    private LinearLayout tabHome, tabCalendar, tabProfile;
    private FrameLayout tabCreatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        btnBack = findViewById(R.id.btnBack);
        filter7D = findViewById(R.id.filter7D);
        filter30D = findViewById(R.id.filter30D);
        filter90D = findViewById(R.id.filter90D);
        filter1Y = findViewById(R.id.filter1Y);

        tabHome = findViewById(R.id.tabHome);
        tabCalendar = findViewById(R.id.tabCalendar);
        tabCreatePost = findViewById(R.id.tabCreatePost);
        tabProfile = findViewById(R.id.tabProfile);

        btnBack.setOnClickListener(v -> finish());

        // Timeframe selector interaction
        filter7D.setOnClickListener(v -> selectFilter(filter7D));
        filter30D.setOnClickListener(v -> selectFilter(filter30D));
        filter90D.setOnClickListener(v -> selectFilter(filter90D));
        filter1Y.setOnClickListener(v -> selectFilter(filter1Y));

        // Bottom Navigation Links
        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        tabCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();
        });

        tabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void selectFilter(TextView selected) {
        TextView[] filters = {filter7D, filter30D, filter90D, filter1Y};
        for (TextView f : filters) {
            f.setBackgroundResource(R.drawable.bg_filter_inactive);
            f.setTextColor(0xFFB9B2C9);
        }
        selected.setBackgroundResource(R.drawable.bg_filter_active);
        selected.setTextColor(0xFFFFFFFF);
    }
}