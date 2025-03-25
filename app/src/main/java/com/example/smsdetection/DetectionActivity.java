package com.example.smsdetection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.smsdetection.adapter.SmsAdapter;
import com.example.smsdetection.entity.SmsInfo;
import com.example.smsdetection.utils.ChatClient;
import com.example.smsdetection.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class DetectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ProgressBar progressBar;
    private TextView progressText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private SmsAdapter mSmsAdapter;
    private List<SmsInfo> mSmsList;
    private ListView lv_sms;
    private CheckBox checkBox_select_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        lv_sms = findViewById(R.id.lv_sms);
        checkBox_select_all = findViewById(R.id.select_all);


        // 初始化 mSmsList
        mSmsList = new ArrayList<>();
        mSmsAdapter = new SmsAdapter(this, mSmsList);
        lv_sms.setAdapter(mSmsAdapter);

        lv_sms.setOnItemClickListener(this);
        lv_sms.setOnItemLongClickListener(this);

        // 启动短信读取任务
        new Thread(this::loadSmsMessages).start();

    }

    private void loadSmsMessages() {
        List<SmsInfo> smsList = readSmsMessages();
        int totalMessages = smsList.size();
        // 逐条展示短信
        for (int i = 0; i < totalMessages; i++) {
            final int progress = (i + 1) * 100 / totalMessages;
            final SmsInfo smsInfo = smsList.get(i);

            // 更新 UI
            handler.postDelayed(() -> {
                mSmsList.add(0, smsInfo); // 添加短信到列表
                mSmsAdapter.notifyDataSetChanged(); // 刷新 Adapter
                progressBar.setProgress(progress); // 更新进度条
                progressText.setText(progress + "%\n"); // 更新进度文本
            }, i * 100L); // 每条短信之间间隔 0.01 秒

            // 最后一条短信加载完成后隐藏 ProgressBar
            if (i == totalMessages - 1) {
                handler.postDelayed(() -> {
                    onLoadingComplete();
                }, i * 100L);

            }
        }

    }

    private List<SmsInfo> readSmsMessages() {
        Uri uri = Uri.parse("content://sms");
        List<SmsInfo> smsList = new ArrayList<>();

        // 检查 uri 是否为空
        if (uri == null) {
            return smsList; // 返回空列表，而不是 null
        }

//        // 通过内容解析器获取符合条件的结果集游标
//        Context context = getApplicationContext();
//        Cursor cursor = context.getContentResolver().query(
//                uri,
//                new String[]{"address", "body", "date"},
//                null,
//                null,
//                "date DESC"
//        );

//        Cursor cursor = getContentResolver().query(
//                uri,
//                new String[]{"address", "body", "date"},
//                null,
//                null,
//                "date ASC" // 按时间从久远到最新排序
//        );
//
//        // 检查 cursor 是否为空
//        if (cursor == null) {
//            return smsList; // 返回空列表，而不是 null
//        }
//
//        // 遍历 cursor
//        while (cursor.moveToNext()) {
//            // 短信的发送号码
//            String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//            // 短信内容
//            String content = cursor.getString(cursor.getColumnIndexOrThrow("body"));
//            // 短信日期
//            long date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
//
//            // 打印日志
//            Log.d("DEBUG", String.format("sender:%s, content:%s, date:%d", sender, content, date));
//
//            // 创建 SmsInfo 对象
//            SmsInfo info = new SmsInfo();
////            info.datetime = Utils.getDate(Calendar.getInstance()) + "=" + Utils.getNowTime();
//            info.datetime = Utils.formatDate(date) + "=" + Utils.formatTime(date);
//            info.sender = sender;
//            info.content = content;
//            info.type = SmsInfo.SMS_TYPE_COMMON;
//
//            // 将 SmsInfo 对象添加到列表
//            smsList.add(info);
//        }
//
//        // 关闭 cursor
//        cursor.close();

        // 随机数据测试代码
        String[] senders = {"10086", "+1234567890", "Bank Alert", "Mom", "Spam Caller"};
        String[] messages = {
                "Your bill is due on the 25th.",
                "Hey, let's catch up this weekend!",
                "Transaction Alert: $500 deducted.",
                "Dinner is ready! Come home soon.",
                "You won a lottery! Click here to claim your prize."
        };

        Random random = new Random();

        for (int i = 0; i < 10; i++) { // 生成10条测试短信
            SmsInfo info = new SmsInfo();
            long randomTimestamp = System.currentTimeMillis() - random.nextInt(1000000000);

            info.sender = senders[random.nextInt(senders.length)];
            info.content = messages[random.nextInt(messages.length)];
            info.datetime = Utils.formatDate(randomTimestamp) + "=" + Utils.formatTime(randomTimestamp);
            info.type = SmsInfo.SMS_TYPE_COMMON;

            smsList.add(info);
        }

        return smsList;
    }

    private void onLoadingComplete() {
        // 1. 将 ProgressBar 的背景设置为绿色圆圈
        progressBar.setBackgroundResource(R.drawable.ic_checkmark);

        progressText.setVisibility(View.GONE);

        // 3. 启动向上移动并消失的动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_up_and_fade_out);
        progressBar.startAnimation(animation);

        // 4. 动画结束后隐藏 ProgressBar 和勾的图像
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

    }

    // Todo 还需实现，怎么把数据传输过去
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent( DetectionActivity.this, SmsDetailActivity.class);
        intent.putExtra("sms_id", mSmsList.get(position).id);
        startActivity(intent);
    }


    // Todo
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSmsAdapter.setEditMode(true);
        checkBox_select_all.setVisibility(View.VISIBLE);
        return true;
    }

}