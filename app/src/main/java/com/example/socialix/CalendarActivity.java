package com.example.socialix;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialix.models.PostModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnPrevMonth, btnNextMonth, tvMonthYear, tvSelectedDatePostsHeader;
    private LinearLayout layoutDaysGrid, layoutPostsContainer;
    private LinearLayout tabHome, tabAnalytics, tabProfile;
    private FrameLayout tabCreatePost;

    private Calendar currentDisplayCalendar;
    private int selectedDay = 1;
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat dayKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initViews();
        setupNavigation();

        // Default to current date
        currentDisplayCalendar = Calendar.getInstance();
        selectedDay = currentDisplayCalendar.get(Calendar.DAY_OF_MONTH);

        // Fetch latest data from backend, then update UI
        DataManager.getInstance().syncFeedFromCloud(this, success -> {
            runOnUiThread(this::renderCalendarAndPosts);
        });

        renderCalendarAndPosts();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvSelectedDatePostsHeader = findViewById(R.id.tvSelectedDatePostsHeader);
        layoutDaysGrid = findViewById(R.id.layoutDaysGrid);
        layoutPostsContainer = findViewById(R.id.layoutPostsContainer);

        tabHome = findViewById(R.id.tabHome);
        tabCreatePost = findViewById(R.id.tabCreatePost);
        tabAnalytics = findViewById(R.id.tabAnalytics);
        tabProfile = findViewById(R.id.tabProfile);
    }

    private void setupNavigation() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        btnPrevMonth.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.MONTH, -1);
            selectedDay = 1;
            renderCalendarAndPosts();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentDisplayCalendar.add(Calendar.MONTH, 1);
            selectedDay = 1;
            renderCalendarAndPosts();
        });

        tabHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabCreatePost.setOnClickListener(v -> {
            startActivity(new Intent(this, CreatePostActivity.class));
        });

        tabAnalytics.setOnClickListener(v -> {
            startActivity(new Intent(this, AnalyticsActivity.class));
            finish();
        });

        tabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void renderCalendarAndPosts() {
        tvMonthYear.setText(monthYearFormat.format(currentDisplayCalendar.getTime()));
        buildDaysGrid();
        loadPostsForSelectedDate();
    }

    private void buildDaysGrid() {
        layoutDaysGrid.removeAllViews();

        Calendar cal = (Calendar) currentDisplayCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 for Sunday
        int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int currentDay = 1;
        boolean finished = false;

        while (!finished) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 100));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setWeightSum(7f);
            row.setGravity(Gravity.CENTER_VERTICAL);

            for (int col = 0; col < 7; col++) {
                FrameLayout cell = new FrameLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                cell.setLayoutParams(lp);

                if ((layoutDaysGrid.getChildCount() == 0 && col < firstDayOfWeek) || currentDay > maxDaysInMonth) {
                    // Empty spacer cell before the 1st or after the month ends
                } else {
                    final int dayNumber = currentDay;
                    TextView dayText = new TextView(this);
                    FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(85, 85, Gravity.CENTER);
                    dayText.setLayoutParams(textLp);
                    dayText.setGravity(Gravity.CENTER);
                    dayText.setText(String.valueOf(dayNumber));
                    dayText.setTextSize(12f);

                    if (dayNumber == selectedDay) {
                        dayText.setBackgroundResource(R.drawable.bg_date_selected);
                        dayText.setTextColor(Color.WHITE);
                    } else {
                        dayText.setTextColor(Color.WHITE);
                    }

                    cell.setOnClickListener(v -> {
                        selectedDay = dayNumber;
                        buildDaysGrid();
                        loadPostsForSelectedDate();
                    });

                    cell.addView(dayText);
                    currentDay++;
                }
                row.addView(cell);
            }

            layoutDaysGrid.addView(row);
            if (currentDay > maxDaysInMonth) {
                finished = true;
            }
        }
    }

    private void loadPostsForSelectedDate() {
        layoutPostsContainer.removeAllViews();

        Calendar targetCal = (Calendar) currentDisplayCalendar.clone();
        targetCal.set(Calendar.DAY_OF_MONTH, selectedDay);
        String targetDatePrefix = dayKeyFormat.format(targetCal.getTime());

        List<PostModel> allPosts = DataManager.getInstance().getAllPosts();
        List<PostModel> dayPosts = new ArrayList<>();

        if (allPosts != null) {
            for (PostModel post : allPosts) {
                if (post.getScheduledTimestamp() > 0) {
                    String scheduledDate = dayKeyFormat.format(new Date(post.getScheduledTimestamp()));
                    if (scheduledDate.equals(targetDatePrefix)) {
                        dayPosts.add(post);
                    }
                }
            }
        }

        tvSelectedDatePostsHeader.setText(String.format("Posts for %s %d (%d)",
                new SimpleDateFormat("MMM", Locale.getDefault()).format(targetCal.getTime()),
                selectedDay,
                dayPosts.size()));

        if (dayPosts.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No posts scheduled for this day.");
            emptyView.setTextColor(Color.parseColor("#6B6282"));
            emptyView.setTextSize(13f);
            emptyView.setPadding(0, 30, 0, 30);
            emptyView.setGravity(Gravity.CENTER);
            layoutPostsContainer.addView(emptyView);
            return;
        }

        for (PostModel post : dayPosts) {
            layoutPostsContainer.addView(createPostCard(post));
        }
    }

    private LinearLayout createPostCard(PostModel post) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_stat_card_dark);
        card.setPadding(40, 35, 40, 35);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 24);
        card.setLayoutParams(lp);

        // Platform Icon
        TextView icon = new TextView(this);
        icon.setText("📝");
        icon.setTextSize(16f);
        icon.setPadding(0, 0, 24, 0);
        card.addView(icon);

        boolean isPublished = post.getStatus() != null && "PUBLISHED".equalsIgnoreCase(post.getStatus());

        // Content / Time Column
        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView content = new TextView(this);
        content.setText(post.getContent() != null ? post.getContent() : "Post");
        content.setTextColor(Color.WHITE);
        content.setTextSize(13f);
        content.setMaxLines(1);

        String timeStr = isPublished ? "Published" : "Scheduled";
        if (post.getScheduledTimestamp() > 0) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            timeStr = timeFormat.format(new Date(post.getScheduledTimestamp()));
        }

        TextView time = new TextView(this);
        time.setText(timeStr);
        time.setTextColor(Color.parseColor("#B9B2C9"));
        time.setTextSize(11f);

        textCol.addView(content);
        textCol.addView(time);
        card.addView(textCol);

        // Status Badge
        TextView badge = new TextView(this);
        badge.setText(isPublished ? "Published" : "Scheduled");
        badge.setTextColor(isPublished ? Color.parseColor("#38BDF8") : Color.parseColor("#34D399"));
        badge.setBackgroundResource(isPublished ? R.drawable.bg_status_published : R.drawable.bg_status_scheduled);
        badge.setPadding(24, 10, 24, 10);
        badge.setTextSize(11f);
        card.addView(badge);

        return card;
    }
}