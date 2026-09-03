package com.example.socialix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.socialix.models.PostModel;
import com.example.socialix.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etPostContent;
    private TextView btnPlatformTwitter, btnPlatformLinkedIn, btnPlatformInstagram;
    private TextView btnPickDate, btnPickTime, tvSelectedSchedule;
    private TextView btnOpenAiAssistant;
    private AppCompatButton btnSubmitPost;

    private ActivityResultLauncher<Intent> aiAssistantLauncher;

    private final List<String> selectedPlatforms = new ArrayList<>();
    private final Calendar scheduledCalendar = Calendar.getInstance();
    private boolean isDatePicked = false;
    private boolean isTimePicked = false;
    private long finalScheduledTimestamp = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initViews();
        setupAiLauncher();
        setupPlatformSelectors();
        setupDateTimePickers();
        setupSubmitButton();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etPostContent = findViewById(R.id.etPostContent);
        btnPlatformTwitter = findViewById(R.id.btnPlatformTwitter);
        btnPlatformLinkedIn = findViewById(R.id.btnPlatformLinkedIn);
        btnPlatformInstagram = findViewById(R.id.btnPlatformInstagram);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        tvSelectedSchedule = findViewById(R.id.tvSelectedSchedule);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnOpenAiAssistant = findViewById(R.id.btnOpenAiAssistant);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupAiLauncher() {
        aiAssistantLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String caption = result.getData().getStringExtra("caption_text");
                        if (caption != null && !caption.trim().isEmpty()) {
                            etPostContent.setText(caption);
                            etPostContent.setSelection(caption.length());
                        }
                    }
                }
        );

        if (btnOpenAiAssistant != null) {
            btnOpenAiAssistant.setOnClickListener(v -> {
                Intent intent = new Intent(CreatePostActivity.this, AiAssistantActivity.class);
                aiAssistantLauncher.launch(intent);
            });
        }
    }

    private void setupPlatformSelectors() {
        bindPlatformToggle(btnPlatformTwitter, "Twitter");
        bindPlatformToggle(btnPlatformLinkedIn, "LinkedIn");
        bindPlatformToggle(btnPlatformInstagram, "Instagram");
    }

    private void bindPlatformToggle(TextView view, String platformName) {
        if (view == null) return;
        view.setOnClickListener(v -> {
            if (selectedPlatforms.contains(platformName)) {
                selectedPlatforms.remove(platformName);
                view.setBackgroundResource(R.drawable.bg_stat_card_dark);
                view.setTextColor(Color.parseColor("#B9B2C9"));
            } else {
                selectedPlatforms.add(platformName);
                view.setBackgroundResource(R.drawable.bg_gradient_btn);
                view.setTextColor(Color.WHITE);
            }
        });
    }

    private void setupDateTimePickers() {
        Calendar now = Calendar.getInstance();

        if (btnPickDate != null) {
            btnPickDate.setOnClickListener(v -> {
                DatePickerDialog datePicker = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            scheduledCalendar.set(Calendar.YEAR, year);
                            scheduledCalendar.set(Calendar.MONTH, month);
                            scheduledCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            isDatePicked = true;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            btnPickDate.setText(sdf.format(scheduledCalendar.getTime()));
                            btnPickDate.setTextColor(Color.parseColor("#38BDF8"));
                            updateScheduleDisplay();
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            });
        }

        if (btnPickTime != null) {
            btnPickTime.setOnClickListener(v -> {
                TimePickerDialog timePicker = new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {
                            scheduledCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            scheduledCalendar.set(Calendar.MINUTE, minute);
                            scheduledCalendar.set(Calendar.SECOND, 0);
                            scheduledCalendar.set(Calendar.MILLISECOND, 0);
                            isTimePicked = true;

                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            btnPickTime.setText(sdf.format(scheduledCalendar.getTime()));
                            btnPickTime.setTextColor(Color.parseColor("#38BDF8"));
                            updateScheduleDisplay();
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                timePicker.show();
            });
        }
    }

    private void updateScheduleDisplay() {
        if ((isDatePicked || isTimePicked) && tvSelectedSchedule != null && btnSubmitPost != null) {
            finalScheduledTimestamp = scheduledCalendar.getTimeInMillis();
            SimpleDateFormat fullFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            tvSelectedSchedule.setText("Scheduled for: " + fullFormat.format(scheduledCalendar.getTime()));
            tvSelectedSchedule.setTextColor(Color.parseColor("#38BDF8"));
            btnSubmitPost.setText("Schedule Post");
        }
    }

    private void setupSubmitButton() {
        if (btnSubmitPost == null) return;
        btnSubmitPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();

            if (content.isEmpty()) {
                etPostContent.setError("Please enter post content");
                return;
            }

            if (selectedPlatforms.isEmpty()) {
                selectedPlatforms.add("General");
            }

            PostModel post = new PostModel();
            post.setContent(content);
            post.setPlatforms(new ArrayList<>(selectedPlatforms));

            long currentEpoch = System.currentTimeMillis();

            if (isDatePicked && finalScheduledTimestamp > currentEpoch) {
                post.setScheduledTimestamp(finalScheduledTimestamp);
                post.setStatus("SCHEDULED");
            } else {
                post.setScheduledTimestamp(currentEpoch);
                post.setStatus("PUBLISHED");
            }

            savePost(post);
        });
    }

    private void savePost(PostModel post) {
        btnSubmitPost.setEnabled(false);
        btnSubmitPost.setText("Processing...");

        try {
            ApiClient.getApiService(this).createPost(post).enqueue(new Callback<PostModel>() {
                @Override
                public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        DataManager.getInstance().addPost(response.body());
                    } else {
                        saveLocallyFallback(post);
                    }
                    finishCreation();
                }

                @Override
                public void onFailure(Call<PostModel> call, Throwable t) {
                    saveLocallyFallback(post);
                    finishCreation();
                }
            });
        } catch (Exception e) {
            saveLocallyFallback(post);
            finishCreation();
        }
    }

    private void saveLocallyFallback(PostModel post) {
        if (post.getId() == null || post.getId().isEmpty()) {
            post.setId(String.valueOf(System.currentTimeMillis()));
        }
        DataManager.getInstance().addPost(post);
    }

    private void finishCreation() {
        Toast.makeText(this, "Post successfully dispatched!", Toast.LENGTH_SHORT).show();
        finish();
    }
}