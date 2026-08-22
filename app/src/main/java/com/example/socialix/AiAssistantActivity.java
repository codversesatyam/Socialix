package com.example.socialix;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AiAssistantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_assistant);

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

        // Header Actions
        View btnClearChat = findViewById(R.id.btnClearChat);
        if (btnClearChat != null) {
            btnClearChat.setOnClickListener(v -> Toast.makeText(this, "Chat cleared", Toast.LENGTH_SHORT).show());
        }

        // Prompt Chips & Input
        EditText etPromptInput = findViewById(R.id.etPromptInput);
        View promptChip1 = findViewById(R.id.promptChip1);
        View promptChip2 = findViewById(R.id.promptChip2);
        View promptChip3 = findViewById(R.id.promptChip3);

        if (etPromptInput != null) {
            if (promptChip1 != null) {
                promptChip1.setOnClickListener(v -> etPromptInput.setText("Generate a viral caption for an Android developer launching a new app."));
            }
            if (promptChip2 != null) {
                promptChip2.setOnClickListener(v -> etPromptInput.setText("Suggest 10 trending hashtags for modern software development."));
            }
            if (promptChip3 != null) {
                promptChip3.setOnClickListener(v -> etPromptInput.setText("Rewrite this post with a professional and engaging tone: "));
            }
        }

        // Send Button Action
        View btnSendPrompt = findViewById(R.id.btnSendPrompt);
        if (btnSendPrompt != null && etPromptInput != null) {
            btnSendPrompt.setOnClickListener(v -> {
                String text = etPromptInput.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "AI is generating response...", Toast.LENGTH_SHORT).show();
                etPromptInput.setText("");
            });
        }

        // Copy Sample AI Response
        View btnCopySample = findViewById(R.id.btnCopySample);
        if (btnCopySample != null) {
            btnCopySample.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Response", "Big news! Today we are introducing Socialix...");
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Use in Post Action
        View btnUseInPost = findViewById(R.id.btnUseInPost);
        if (btnUseInPost != null) {
            btnUseInPost.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));
        }

        // --- Bottom Navigation Handlers ---
        View tabHome = findViewById(R.id.tabHome);
        View tabCalendar = findViewById(R.id.tabCalendar);
        View tabCreatePost = findViewById(R.id.tabCreatePost);
        View tabAnalytics = findViewById(R.id.tabAnalytics);
        View tabProfile = findViewById(R.id.tabProfile);

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            });
        }

        if (tabCalendar != null) {
            tabCalendar.setOnClickListener(v -> {
                startActivity(new Intent(this, CalendarActivity.class));
                finish();
            });
        }

        if (tabCreatePost != null) {
            tabCreatePost.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));
        }

        if (tabAnalytics != null) {
            tabAnalytics.setOnClickListener(v -> {
                startActivity(new Intent(this, AnalyticsActivity.class));
                finish();
            });
        }

        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            });
        }
    }
}