package com.example.socialix;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialix.models.NotificationItem;
import com.example.socialix.models.PostModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etPostContent;
    private TextView tvCharCounter, chipImprove, chipHashtags, chipShorten;
    private AppCompatButton btnSaveDraft, btnSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);

        // System bar insets
        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        btnBack = findViewById(R.id.btnBack);
        etPostContent = findViewById(R.id.etPostContent);
        tvCharCounter = findViewById(R.id.tvCharCounter);
        chipImprove = findViewById(R.id.chipImprove);
        chipHashtags = findViewById(R.id.chipHashtags);
        chipShorten = findViewById(R.id.chipShorten);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnSchedule = findViewById(R.id.btnSchedule);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Live Character Counter
        if (etPostContent != null && tvCharCounter != null) {
            etPostContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tvCharCounter.setText(s.length() + "/2200");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // AI Quick Actions
        if (chipImprove != null) {
            chipImprove.setOnClickListener(v ->
                    Toast.makeText(this, "AI: Improving content clarity & tone...", Toast.LENGTH_SHORT).show()
            );
        }

        if (chipHashtags != null && etPostContent != null) {
            chipHashtags.setOnClickListener(v -> {
                String current = etPostContent.getText().toString();
                etPostContent.setText(current + (current.isEmpty() ? "" : " ") + "#AndroidDev #Socialix #AI #Design");
                etPostContent.setSelection(etPostContent.getText().length());
            });
        }

        if (chipShorten != null) {
            chipShorten.setOnClickListener(v ->
                    Toast.makeText(this, "AI: Shortening post for better engagement...", Toast.LENGTH_SHORT).show()
            );
        }

        // Save as Draft Action
        if (btnSaveDraft != null && etPostContent != null) {
            btnSaveDraft.setOnClickListener(v -> {
                String text = etPostContent.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Please enter post content first", Toast.LENGTH_SHORT).show();
                    return;
                }

                PostModel draftPost = new PostModel(
                        UUID.randomUUID().toString(),
                        text,
                        Collections.singletonList("Draft"),
                        System.currentTimeMillis(),
                        "DRAFT"
                );

                DataManager.getInstance().addPost(draftPost);
                Toast.makeText(this, "Draft saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Schedule Post Action
        if (btnSchedule != null && etPostContent != null) {
            btnSchedule.setOnClickListener(v -> {
                String text = etPostContent.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Please enter post content", Toast.LENGTH_SHORT).show();
                    return;
                }

                PostModel newPost = new PostModel(
                        UUID.randomUUID().toString(),
                        text,
                        Arrays.asList("LinkedIn", "Instagram"),
                        System.currentTimeMillis() + 7200000,
                        "SCHEDULED"
                );

                DataManager.getInstance().addPost(newPost);

                // Add Notification Record
                NotificationItem alert = new NotificationItem(
                        UUID.randomUUID().toString(),
                        "Post Scheduled Successfully",
                        "Your scheduled post has been added to the queue.",
                        "Just now",
                        NotificationItem.Type.PUBLISHED
                );
                DataManager.getInstance().getNotifications().add(0, alert);

                Toast.makeText(this, "Post scheduled successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}