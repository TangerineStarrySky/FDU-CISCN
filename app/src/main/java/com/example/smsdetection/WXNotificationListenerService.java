package com.example.smsdetection;

import static com.example.smsdetection.utils.ChatClient.callWithMessage;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.smsdetection.utils.ChatClient;

@SuppressLint("OverrideAbstract")
public class WXNotificationListenerService extends NotificationListenerService {
    private static final String WX_PACKAGE_NAME = "com.tencent.mm"; // 微信包名
    private static final String CHANNEL_ID = "wx_monitor_channel"; // 修改为微信专用渠道
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(WX_PACKAGE_NAME)) return;

        Notification notification = sbn.getNotification();
        if (notification == null) return;

        // 解析微信通知内容（需适配微信通知结构）
        String title = notification.extras.getString(Notification.EXTRA_TITLE);
        String content = notification.extras.getString(Notification.EXTRA_TEXT);
        if (content == null || content.isEmpty()) return;

        // 调用 AI 分析
        String fraudResult;
        try {
            fraudResult = callWithMessage(content, ChatClient.QWEN1_5b);
        } catch (NoApiKeyException | InputRequiredException e) {
            Log.e("WXMonitor", "AI 分析失败：" + e.getMessage());
            return;
        }

        boolean isFraud = fraudResult.startsWith("是");
        if (isFraud) {
            showFraudAlert(title, content);
        } else {
            showNotification("微信新消息", content);
        }
    }

    private void showFraudAlert(String title, String content) {
        handler.post(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("⚠️ 微信诈骗风险警告！")
                    .setContentText("[" + title + "] 可能包含诈骗信息")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(Color.RED);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        });
    }

    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "微信消息监控",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("实时监控微信消息并分析风险");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}