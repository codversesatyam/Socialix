package com.example.socialix;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.socialix.models.AnalyticsModel;
import com.example.socialix.network.ApiClient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTotalReachValue, badgeGrowth;
    private TextView btnFilter7D, btnFilter30D, btnFilter90D, btnFilter1Y;
    private LineChart lineChart;
    private LinearLayout layoutPlatformContainer;
    private LinearLayout tabHome, tabCalendar, tabProfile;
    private FrameLayout tabCreatePost;
    private String selectedRange = "7D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        initViews();
        setupChartStyling();
        setupFilterListeners();
        setupNavListeners();

        loadAnalytics(selectedRange);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTotalReachValue = findViewById(R.id.tvTotalReachValue);
        badgeGrowth = findViewById(R.id.badgeGrowth);
        lineChart = findViewById(R.id.lineChart);
        layoutPlatformContainer = findViewById(R.id.layoutPlatformContainer);

        btnFilter7D = findViewById(R.id.btnFilter7D);
        btnFilter30D = findViewById(R.id.btnFilter30D);
        btnFilter90D = findViewById(R.id.btnFilter90D);
        btnFilter1Y = findViewById(R.id.btnFilter1Y);

        tabHome = findViewById(R.id.tabHome);
        tabCalendar = findViewById(R.id.tabCalendar);
        tabCreatePost = findViewById(R.id.tabCreatePost);
        tabProfile = findViewById(R.id.tabProfile);
    }

    private void setupChartStyling() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#B9B2C9"));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#B9B2C9"));
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.parseColor("#221D38"));
    }

    private void setupFilterListeners() {
        btnFilter7D.setOnClickListener(v -> updateRangeFilter("7D", btnFilter7D));
        btnFilter30D.setOnClickListener(v -> updateRangeFilter("30D", btnFilter30D));
        btnFilter90D.setOnClickListener(v -> updateRangeFilter("90D", btnFilter90D));
        btnFilter1Y.setOnClickListener(v -> updateRangeFilter("1Y", btnFilter1Y));
    }

    private void updateRangeFilter(String range, TextView activeBtn) {
        selectedRange = range;
        resetFilterButtons();
        activeBtn.setBackgroundResource(R.drawable.bg_gradient_btn);
        activeBtn.setTextColor(Color.WHITE);
        loadAnalytics(range);
    }

    private void resetFilterButtons() {
        int inactiveColor = Color.parseColor("#6B6282");
        btnFilter7D.setBackground(null);
        btnFilter7D.setTextColor(inactiveColor);
        btnFilter30D.setBackground(null);
        btnFilter30D.setTextColor(inactiveColor);
        btnFilter90D.setBackground(null);
        btnFilter90D.setTextColor(inactiveColor);
        btnFilter1Y.setBackground(null);
        btnFilter1Y.setTextColor(inactiveColor);
    }

    private void loadAnalytics(String range) {
        ApiClient.getApiService(this).getAnalytics(range).enqueue(new Callback<AnalyticsModel>() {
            @Override
            public void onResponse(Call<AnalyticsModel> call, Response<AnalyticsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderAnalytics(response.body());
                }
            }

            @Override
            public void onFailure(Call<AnalyticsModel> call, Throwable t) {
                Toast.makeText(AnalyticsActivity.this, "Failed to load live data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderAnalytics(AnalyticsModel data) {
        tvTotalReachValue.setText(String.valueOf(data.getTotalReach()));
        badgeGrowth.setText(String.format("+%.1f%%", data.getGrowthPercentage()));

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        if (data.getChartPoints() != null) {
            for (int i = 0; i < data.getChartPoints().size(); i++) {
                AnalyticsModel.DataPoint point = data.getChartPoints().get(i);
                entries.add(new Entry(i, point.getValue()));
                labels.add(point.getLabel());
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Posts");
        dataSet.setColor(Color.parseColor("#A855F7"));
        dataSet.setCircleColor(Color.parseColor("#38BDF8"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.animateY(600);
        lineChart.invalidate();

        renderPlatforms(data.getPlatformBreakdown());
    }

    private void renderPlatforms(Map<String, Long> platforms) {
        layoutPlatformContainer.removeAllViews();
        if (platforms == null || platforms.isEmpty()) return;

        long maxVal = platforms.values().stream().max(Long::compare).orElse(1L);

        for (Map.Entry<String, Long> entry : platforms.entrySet()) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setBackgroundResource(R.drawable.bg_stat_card_dark);
            item.setPadding(32, 24, 32, 24);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 24);
            item.setLayoutParams(lp);

            TextView title = new TextView(this);
            title.setText(entry.getKey() + ": " + entry.getValue() + " posts");
            title.setTextColor(Color.WHITE);
            title.setTextSize(13f);

            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax((int) maxVal);
            pb.setProgress(entry.getValue().intValue());
            pb.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.bg_progress_gradient));

            LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 16);
            pbLp.setMargins(0, 16, 0, 0);
            pb.setLayoutParams(pbLp);

            item.addView(title);
            item.addView(pb);
            layoutPlatformContainer.addView(item);
        }
    }

    private void setupNavListeners() {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabCalendar.setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarActivity.class));
            finish();
        });

        tabCreatePost.setOnClickListener(v -> {
            startActivity(new Intent(this, CreatePostActivity.class));
        });

        tabProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}