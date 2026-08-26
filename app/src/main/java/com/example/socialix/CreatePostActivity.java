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
import com.example.socialix.network.ApiClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // System bar insets handling
        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        // View Bindings
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

        // AI Working
        if (chipImprove != null) {
            chipImprove.setOnClickListener(v ->
                    Toast.makeText(this, "AI: Improving clarity & engagement tone...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "AI: Shortening post for punchy delivery...", Toast.LENGTH_SHORT).show()
            );
        }

        //  Save as Local Draft
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
                Toast.makeText(this, "Draft saved locally!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Schedule Post to Spring Boot Backend
        if (btnSchedule != null && etPostContent != null) {
            btnSchedule.setOnClickListener(v -> {
                String text = etPostContent.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Please enter post content", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSchedule.setEnabled(false);
                btnSchedule.setText("Scheduling...");

                PostModel newPost = new PostModel(
                        null,
                        text,
                        Arrays.asList("LinkedIn", "Instagram"),
                        System.currentTimeMillis() + 7200000,
                        "SCHEDULED"
                );

                ApiClient.getApiService(this).createPost(newPost).enqueue(new Callback<PostModel>() {
                    @Override
                    public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                        btnSchedule.setEnabled(true);
                        btnSchedule.setText("Schedule");

                        if (response.isSuccessful() && response.body() != null) {
                            DataManager.getInstance().addPost(response.body());

                            NotificationItem alert = new NotificationItem(
                                    UUID.randomUUID().toString(),
                                    "Post Scheduled to Cloud",
                                    "Your post was successfully persisted in PostgreSQL.",
                                    "Just now",
                                    NotificationItem.Type.PUBLISHED
                            );
                            DataManager.getInstance().getNotifications().add(0, alert);

                            Toast.makeText(CreatePostActivity.this, "Saved to Cloud DB!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Fallback to local cache if backend is unreachable
                            fallbackLocalSchedule(newPost);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostModel> call, Throwable t) {
                        btnSchedule.setEnabled(true);
                        btnSchedule.setText("Schedule");
                        // Fallback to local cache when server offline
                        fallbackLocalSchedule(newPost);
                    }
                });
            });
        }
    }

    private void fallbackLocalSchedule(PostModel post) {
        post = new PostModel(
                UUID.randomUUID().toString(),
                post.getContent(),
                post.getPlatforms(),
                post.getScheduledTimestamp(),
                post.getStatus()
        );
        DataManager.getInstance().addPost(post);

        NotificationItem alert = new NotificationItem(
                UUID.randomUUID().toString(),
                "Post Scheduled (Offline)",
                "Post cached locally in data queue.",
                "Just now",
                NotificationItem.Type.PUBLISHED
        );
        DataManager.getInstance().getNotifications().add(0, alert);

        Toast.makeText(this, "Post scheduled in local queue!", Toast.LENGTH_SHORT).show();
        finish();
    }
}