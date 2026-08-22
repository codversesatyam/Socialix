package com.example.socialix;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialix.models.NotificationItem;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private LinearLayout notificationsContainer;
    private TextView chipAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        View root = findViewById(android.R.id.content);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });
        }

        notificationsContainer = findViewById(R.id.notificationsContainer);
        chipAll = findViewById(R.id.chipAll);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        TextView btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        if (btnMarkAllRead != null) {
            btnMarkAllRead.setOnClickListener(v -> {
                DataManager.getInstance().markAllNotificationsRead();
                renderNotifications();
                Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
            });
        }

        renderNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderNotifications();
    }

    private void renderNotifications() {
        if (notificationsContainer == null) return;
        notificationsContainer.removeAllViews();

        List<NotificationItem> list = DataManager.getInstance().getNotifications();

        if (chipAll != null) {
            chipAll.setText("All (" + list.size() + ")");
        }

        int dp14 = (int) (14 * getResources().getDisplayMetrics().density);
        int dp12 = (int) (12 * getResources().getDisplayMetrics().density);
        int dp10 = (int) (10 * getResources().getDisplayMetrics().density);
        int dp40 = (int) (40 * getResources().getDisplayMetrics().density);
        int dp20 = (int) (20 * getResources().getDisplayMetrics().density);
        int dp8 = (int) (8 * getResources().getDisplayMetrics().density);

        for (NotificationItem item : list) {
            // Card Container
            RelativeLayout card = new RelativeLayout(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, dp10);
            card.setLayoutParams(cardParams);
            card.setBackgroundResource(R.drawable.bg_notif_card);
            card.setPadding(dp14, dp14, dp14, dp14);

            // Icon Frame
            FrameLayout iconFrame = new FrameLayout(this);
            iconFrame.setId(View.generateViewId());
            RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(dp40, dp40);
            iconParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            iconFrame.setLayoutParams(iconParams);
            iconFrame.setBackgroundResource(R.drawable.bg_account_chip_dark);

            ImageView icon = new ImageView(this);
            FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(dp20, dp20, Gravity.CENTER);
            icon.setLayoutParams(imgParams);

            if (item.getType() == NotificationItem.Type.GROWTH) {
                icon.setImageResource(R.drawable.ic_notif_growth);
            } else if (item.getType() == NotificationItem.Type.MESSAGE) {
                icon.setImageResource(R.drawable.ic_notif_chat);
            } else {
                icon.setImageResource(R.drawable.ic_notif_published);
            }
            iconFrame.addView(icon);
            card.addView(iconFrame);

            // Unread Dot Indicator
            if (!item.isRead()) {
                View dot = new View(this);
                dot.setId(View.generateViewId());
                RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(dp8, dp8);
                dotParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                dotParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
                dot.setLayoutParams(dotParams);
                dot.setBackgroundResource(R.drawable.dot_unread);
                card.addView(dot);
            }

            // Text Block
            LinearLayout textBlock = new LinearLayout(this);
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.addRule(RelativeLayout.END_OF, iconFrame.getId());
            textParams.setMargins(dp12, 0, dp12, 0);
            textBlock.setLayoutParams(textParams);
            textBlock.setOrientation(LinearLayout.VERTICAL);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(item.getTitle());
            tvTitle.setTextColor(0xFFFFFFFF);
            tvTitle.setTextSize(13);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            textBlock.addView(tvTitle);

            TextView tvDesc = new TextView(this);
            tvDesc.setText(item.getDescription());
            tvDesc.setTextColor(0xFFB9B2C9);
            tvDesc.setTextSize(12);
            tvDesc.setPadding(0, (int) (3 * getResources().getDisplayMetrics().density), 0, 0);
            textBlock.addView(tvDesc);

            TextView tvTime = new TextView(this);
            tvTime.setText(item.getTimeAgo());
            tvTime.setTextColor(0xFF6B6282);
            tvTime.setTextSize(11);
            tvTime.setPadding(0, (int) (4 * getResources().getDisplayMetrics().density), 0, 0);
            textBlock.addView(tvTime);

            card.addView(textBlock);

            // Mark single item read on tap
            card.setOnClickListener(v -> {
                item.setRead(true);
                renderNotifications();
            });

            notificationsContainer.addView(card);
        }
    }
}