package com.example.socialix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnPrevMonth, btnNextMonth;
    private LinearLayout tabHome, tabAnalytics, tabProfile;
    private FrameLayout tabCreatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tabHome = findViewById(R.id.tabHome);
        tabCreatePost = findViewById(R.id.tabCreatePost);
        tabAnalytics = findViewById(R.id.tabAnalytics);
        tabProfile = findViewById(R.id.tabProfile);

        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> Toast.makeText(this, "Previous Month", Toast.LENGTH_SHORT).show());
        btnNextMonth.setOnClickListener(v -> Toast.makeText(this, "Next Month", Toast.LENGTH_SHORT).show());

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        tabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        tabAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });



        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}