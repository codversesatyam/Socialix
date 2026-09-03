package com.example.socialix;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiAssistantActivity extends AppCompatActivity {

    private static final String TAG = "AiAssistantActivity";
    // 10.0.2.2 maps to localhost on host machine from Android Emulator
    private static final String API_URL = "http://10.0.2.2:8080/api/ai/generate-caption";

    private ImageView btnBack;
    private TextView btnClearChat;
    private Spinner spPlatform, spTone;
    private EditText etPromptInput;
    private FrameLayout btnSendPrompt;
    private LinearLayout chatContainer;
    private ProgressBar progressBar;

    private TextView promptChip1, promptChip2, promptChip3;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClearChat = findViewById(R.id.btnClearChat);
        spPlatform = findViewById(R.id.spPlatform);
        spTone = findViewById(R.id.spTone);
        etPromptInput = findViewById(R.id.etPromptInput);
        btnSendPrompt = findViewById(R.id.btnSendPrompt);
        chatContainer = findViewById(R.id.chatContainer);
        progressBar = findViewById(R.id.progressBar);

        promptChip1 = findViewById(R.id.promptChip1);
        promptChip2 = findViewById(R.id.promptChip2);
        promptChip3 = findViewById(R.id.promptChip3);
    }

    private void setupSpinners() {
        String[] platforms = {"LinkedIn", "Twitter / X", "Instagram"};
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, platforms);
        platformAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        if (spPlatform != null) spPlatform.setAdapter(platformAdapter);

        String[] tones = {"Professional", "Casual", "Enthusiastic", "Educational"};
        ArrayAdapter<String> toneAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, tones);
        toneAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        if (spTone != null) spTone.setAdapter(toneAdapter);
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnClearChat != null) {
            btnClearChat.setOnClickListener(v -> chatContainer.removeAllViews());
        }

        if (btnSendPrompt != null) {
            btnSendPrompt.setOnClickListener(v -> generateAiCaption());
        }

        // Prompt Chips
        if (promptChip1 != null) {
            promptChip1.setOnClickListener(v -> setPromptText(promptChip1.getText().toString()));
        }
        if (promptChip2 != null) {
            promptChip2.setOnClickListener(v -> setPromptText(promptChip2.getText().toString()));
        }
        if (promptChip3 != null) {
            promptChip3.setOnClickListener(v -> setPromptText(promptChip3.getText().toString()));
        }
    }

    private void setPromptText(String text) {
        if (etPromptInput != null) {
            etPromptInput.setText(text);
            etPromptInput.setSelection(etPromptInput.getText().length());
        }
    }

    private void generateAiCaption() {
        String topic = etPromptInput.getText().toString().trim();
        String platform = spPlatform != null ? spPlatform.getSelectedItem().toString() : "LinkedIn";
        String tone = spTone != null ? spTone.getSelectedItem().toString() : "Professional";

        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a prompt or select a chip", Toast.LENGTH_SHORT).show();
            return;
        }

        appendUserBubble(topic);
        etPromptInput.setText("");

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (btnSendPrompt != null) btnSendPrompt.setEnabled(false);

        executorService.execute(() -> {
            String response = makeApiRequest(topic, platform, tone);

            mainHandler.post(() -> {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (btnSendPrompt != null) btnSendPrompt.setEnabled(true);

                if (response != null) {
                    appendAiBubble(response);
                } else {
                    appendAiBubble("⚠️ Could not reach the backend service. Please check Spring Boot server at port 8080.");
                }
            });
        });
    }

    private String makeApiRequest(String topic, String platform, String tone) {
        HttpURLConnection conn = null;
        try {
            Log.d(TAG, "Connecting to: " + API_URL);
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("topic", topic);
            jsonParam.put("platform", platform);
            jsonParam.put("tone", tone);

            Log.d(TAG, "Request JSON: " + jsonParam.toString());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                Log.d(TAG, "Response String: " + response.toString());
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.optString("caption", "No caption returned.");
            } else {
                Log.e(TAG, "Server returned error status code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Network request failure: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    private void appendUserBubble(String message) {
        TextView userMsg = new TextView(this);
        userMsg.setText(message);
        userMsg.setTextColor(0xFFFFFFFF);
        userMsg.setTextSize(13);
        userMsg.setPadding(30, 20, 30, 20);
        userMsg.setBackgroundResource(R.drawable.bg_user_bubble);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        params.gravity = android.view.Gravity.END;
        layout.setLayoutParams(params);
        layout.addView(userMsg);

        chatContainer.addView(layout);
    }

    private void appendAiBubble(String responseText) {
        LinearLayout aiBubbleLayout = new LinearLayout(this);
        aiBubbleLayout.setOrientation(LinearLayout.VERTICAL);
        aiBubbleLayout.setBackgroundResource(R.drawable.bg_ai_bubble);
        aiBubbleLayout.setPadding(30, 20, 30, 20);

        TextView aiMsg = new TextView(this);
        aiMsg.setText(responseText);
        aiMsg.setTextColor(0xFFFFFFFF);
        aiMsg.setTextSize(13);
        aiBubbleLayout.addView(aiMsg);

        // Action Buttons Row (Copy & Use in Post)
        LinearLayout actionRow = new LinearLayout(this);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 16, 0, 0);
        actionRow.setLayoutParams(rowParams);

        TextView btnCopy = new TextView(this);
        btnCopy.setText("Copy");
        btnCopy.setTextColor(0xFFB9B2C9);
        btnCopy.setTextSize(11);
        btnCopy.setPadding(20, 8, 20, 8);
        btnCopy.setBackgroundResource(R.drawable.bg_ai_chip_dark);
        btnCopy.setOnClickListener(v -> copyToClipboard(responseText));

        TextView btnUse = new TextView(this);
        btnUse.setText("Use in Post");
        btnUse.setTextColor(0xFFA855F7);
        btnUse.setTextSize(11);
        btnUse.setPadding(20, 8, 20, 8);
        LinearLayout.LayoutParams useParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        useParams.setMargins(16, 0, 0, 0);
        btnUse.setLayoutParams(useParams);
        btnUse.setBackgroundResource(R.drawable.bg_ai_chip_dark);
        btnUse.setOnClickListener(v -> useCaptionInPost(responseText));

        actionRow.addView(btnCopy);
        actionRow.addView(btnUse);
        aiBubbleLayout.addView(actionRow);

        LinearLayout wrapper = new LinearLayout(this);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapperParams.setMargins(0, 20, 0, 0);
        wrapperParams.gravity = android.view.Gravity.START;
        wrapper.setLayoutParams(wrapperParams);
        wrapper.addView(aiBubbleLayout);

        chatContainer.addView(wrapper);
    }

    private void copyToClipboard(String text) {
        if (text == null || text.trim().isEmpty()) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("AI Caption", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }

    private void useCaptionInPost(String caption) {
        if (caption != null && !caption.trim().isEmpty()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("caption_text", caption);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "No caption to use!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}