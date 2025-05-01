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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class DetectionSelectionActivity extends ComponentActivity implements View.OnClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener
{
    private TextView title;
    private TextView history;
    private SmsDBHelper mDBHelper;
    private List<SmsInfo> mSmsList;
    private SmsAdapter mSmsAdapter;
    private ListView lv_sms;
    private EditText search_box;
    private Button startButton;
    private LinearLayout long_click_interface;
    private CheckBox checkBox_select_all;

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

        long_click_interface = findViewById(R.id.long_click_interface);

        checkBox_select_all = findViewById(R.id.select_all);
        checkBox_select_all.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            toggleAllItemsSelection(isChecked); // Toggle all items when clicked
        });

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

    private void toggleAllItemsSelection(boolean isChecked) {
        if (mSmsAdapter == null || mSmsList.isEmpty()) return;

        // Update all items' selection state
        for (SmsInfo info : mSmsList) {
            info.isSelected = isChecked;
        }

        // Refresh the adapter and update the "Select All" CheckBox
        mSmsAdapter.notifyDataSetChanged();
        checkBox_select_all.setChecked(isChecked); // Ensure consistency
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
        lv_sms.setOnItemClickListener(this);
        lv_sms.setOnItemLongClickListener(this);
        // 重新计算总数
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid == R.id.ic_back){
            finish();
        } else if (vid == R.id.startButton) {
            if(mSmsAdapter.getEditMode()) {
                mSmsList = mSmsAdapter.getSelectedItems();
            }

            Intent intent = new Intent();
            intent.setClass(DetectionSelectionActivity.this, DetectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ArrayList<SmsInfo> smsArrayList = new ArrayList<>(mSmsList);
            intent.putParcelableArrayListExtra("mSmsList", smsArrayList);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(DetectionSelectionActivity.this, SmsDetailActivity.class);
        intent.putExtra("sms_id", mSmsList.get(position).id);
//        intent.putExtra("chat_state", chatState);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSmsAdapter.setEditMode(true);
        long_click_interface.setVisibility(View.VISIBLE);
        return true;
    }
}
