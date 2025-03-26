package com.example.smsdetection;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smsdetection.adapter.SmsAdapter;
import com.example.smsdetection.database.SmsDBHelper;
import com.example.smsdetection.entity.SmsInfo;
import com.example.smsdetection.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DetectionSelectionActivity extends ComponentActivity implements View.OnClickListener{
    private TextView title;
    private TextView history;
    private SmsDBHelper mDBHelper;
    private ArrayList<SmsInfo> mSmsList;
    private SmsAdapter mSmsAdapter;
    private ListView lv_sms;
    private EditText search_box;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detection_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.tv_title);
        title.setText("检测");
        history = findViewById(R.id.tv_history);
        history.setVisibility(TextView.GONE);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        mDBHelper = SmsDBHelper.getInstance(this);
        findViewById(R.id.ic_back).setOnClickListener(this);

        lv_sms = findViewById(R.id.lv_sms);
        search_box = findViewById(R.id.search_box);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                if (query.isEmpty()) {
                    List<SmsInfo> searchResults = readSmsMessages(""); // 查询所有短信
                    mSmsList.clear();
                    mSmsList.addAll(searchResults);
                    mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
                } else {

                    // 异步查询数据库
                    new Thread(() -> {
                        List<SmsInfo> searchResults = readSmsMessages(query);
                        runOnUiThread(() -> {
                            if (searchResults != null) {
                                mSmsList.clear();
                                mSmsList.addAll(searchResults);
                                mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
                            } else {
                                Log.e("HistoryActivity", "No results found or query error");
                            }
                        });
                    }).start();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSMS();
    }

    private void showSMS() {
        mSmsList = readSmsMessages("");
        Collections.reverse(mSmsList);
        if (mSmsList.size() == 0) {
            return;
        }
        mSmsAdapter = new SmsAdapter(this, mSmsList);
        lv_sms.setAdapter(mSmsAdapter);
        // 给列表项设置监听
//        lv_sms.setOnItemClickListener(this);
//        lv_sms.setOnItemLongClickListener(this);
        // 重新计算总数
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid == R.id.ic_back){
            finish();
        } else if (vid == R.id.startButton) {
            Intent intent = new Intent();
            intent.setClass(DetectionSelectionActivity.this, DetectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putParcelableArrayListExtra("mSmsList", mSmsList);
            startActivity(intent);
        }
    }

    private ArrayList<SmsInfo> readSmsMessages(String query) {
        Uri uri = Uri.parse("content://sms");
        ArrayList<SmsInfo> smsList = new ArrayList<>();

//        // 检查 uri 是否为空
//        if (uri == null) {
//            return smsList; // 返回空列表，而不是 null
//        }
//
//        // 通过内容解析器获取符合条件的结果集游标
//        Context context = getApplicationContext();
//        // 在查询时使用 LIKE 语句进行模糊匹配，查询内容和发送者
//        String selection = "body LIKE ? OR address LIKE ?";
//        String[] selectionArgs = new String[] { "%" + query + "%", "%" + query + "%" };
//        Cursor cursor = context.getContentResolver().query(
//                uri,
//                new String[]{"id", "address", "body", "date"},
//                selection,
//                selectionArgs,
//                "date DESC"
//        );
//
//
//
////        Cursor cursor = getContentResolver().query(
////                uri,
////                new String[]{"address", "body", "date"},
////                null,
////                null,
////                "date ASC" // 按时间从久远到最新排序
////        );
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

        for (int i = 0; i < 100; i++) { // 生成10条测试短信
            SmsInfo info = new SmsInfo();
            long randomTimestamp = System.currentTimeMillis() - random.nextInt(1000000000);
            info.id = i;
            info.sender = senders[random.nextInt(senders.length)];
            info.content = messages[random.nextInt(messages.length)];
            info.datetime = Utils.formatDate(randomTimestamp) + "=" + Utils.formatTime(randomTimestamp);
            info.type = SmsInfo.SMS_TYPE_COMMON;

            smsList.add(info);
        }

        return smsList;
    }
}
