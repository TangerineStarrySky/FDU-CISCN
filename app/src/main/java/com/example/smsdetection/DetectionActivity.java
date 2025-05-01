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
import com.example.smsdetection.database.SmsDBHelper;
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
    private SmsDBHelper mDBHelper;

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
//        checkBox_select_all = findViewById(R.id.select_all);

        mDBHelper = SmsDBHelper.getInstance(this);

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
        ArrayList<SmsInfo> smsList = getIntent().getParcelableArrayListExtra("mSmsList");
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
                    for(int k = 0;k < mSmsList.size();k ++) {
                        mDBHelper.save(mSmsList.get(k));
                    }
                }, i * 100L);

            }
        }

    }

    private void onLoadingComplete() {
        // 1. 将 ProgressBar 的背景设置为绿色圆圈
//        progressBar.setBackgroundResource(R.drawable.ic_checkmark);

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