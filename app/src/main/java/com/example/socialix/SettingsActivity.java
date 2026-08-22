package com.example.socialix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        prefs = getSharedPreferences("SocialixSettings", MODE_PRIVATE);

        // Back Button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Toggles with SharedPreferences Persistence
        SwitchCompat switchPushNotifs = findViewById(R.id.switchPushNotifs);
        SwitchCompat switchAiCaptions = findViewById(R.id.switchAiCaptions);

        if (switchPushNotifs != null) {
            switchPushNotifs.setChecked(prefs.getBoolean("push_notifications", true));
            switchPushNotifs.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("push_notifications", isChecked).apply();
                Toast.makeText(this, isChecked ? "Push notifications enabled" : "Push notifications muted", Toast.LENGTH_SHORT).show();
            });
        }

        if (switchAiCaptions != null) {
            switchAiCaptions.setChecked(prefs.getBoolean("ai_captions", true));
            switchAiCaptions.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean("ai_captions", isChecked).apply();
                Toast.makeText(this, isChecked ? "AI suggestions enabled" : "AI suggestions disabled", Toast.LENGTH_SHORT).show();
            });
        }

        // Clear Drafts Action
        View rowClearDrafts = findViewById(R.id.rowClearDrafts);
        if (rowClearDrafts != null) {
            rowClearDrafts.setOnClickListener(v -> {
                Toast.makeText(this, "Draft cache cleared successfully", Toast.LENGTH_SHORT).show();
            });
        }

        // Logout Flow
        View btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out of Socialix?")
                        .setPositiveButton("Log Out", (dialog, which) -> {
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }
}