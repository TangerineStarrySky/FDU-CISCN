package com.example.smsdetection;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smsdetection.adapter.SmsAdapter;
import com.example.smsdetection.database.SmsDBHelper;
import com.example.smsdetection.entity.SmsInfo;
//import com.example.smsdetection.model.AppViewModel;
import com.example.smsdetection.utils.ToastUtil;

import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private SmsDBHelper mDBHelper;
    private List<SmsInfo> mSmsList;
    private SmsAdapter mSmsAdapter;
    private ListView lv_sms;
    private TextView tv_total_num;
    private CheckBox checkBox_select_all;
    private LinearLayout long_click_interface;
    private EditText search_box;

//    private AppViewModel.ChatState chatState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mDBHelper = SmsDBHelper.getInstance(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("历史记录");
        TextView tv_history = findViewById(R.id.tv_history);
        tv_history.setText("");

        findViewById(R.id.ic_back).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);

        lv_sms = findViewById(R.id.lv_sms);
        tv_total_num = findViewById(R.id.tv_total_num);
        long_click_interface = findViewById(R.id.long_click_interface);

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
                    List<SmsInfo> searchResults = mDBHelper.queryAllSmsInfo(); // 查询所有短信
                    mSmsList.clear();
                    mSmsList.addAll(searchResults);
                    mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
                    refreshTotalNum(); // 刷新记录总数
                } else {

                    // 异步查询数据库
                    new Thread(() -> {
                        List<SmsInfo> searchResults = mDBHelper.querySmsInfoBySearch(query);
                        runOnUiThread(() -> {
                            if (searchResults != null) {
                                mSmsList.clear();
                                mSmsList.addAll(searchResults);
                                mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
                                refreshTotalNum(); // 刷新记录总数
                            } else {
                                Log.e("HistoryActivity", "No results found or query error");
                            }
                        });
                    }).start();
                }
            }
        });

        // Initialize the "Select All" CheckBox
        checkBox_select_all = findViewById(R.id.select_all);
        checkBox_select_all.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            toggleAllItemsSelection(isChecked); // Toggle all items when clicked
        });

//        Calendar calendar = Calendar.getInstance();
//        SmsInfo smsInfo = new SmsInfo();
//        smsInfo.sender = "我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我";
//        smsInfo.datetime = Utils.getDate(calendar);
//        smsInfo.type = 0;
//        smsInfo.content = "普通信息测试普通信息测试普通信息测试普通" +
//                "信息测试普通信息测试普通信息测试普通信息测试普" +
//                "信息测试普通信息测试普通信息测试普通信息测试普" +
//                "信息测试普通信息测试普通信息测试普通信息测试普" +
//                "信息测试普通信息测试普通信息测试普通信息测试普" +
//                "通信息测试普通信息测试普通信息测试普通信息测试普通信息测试";
//        mDBHelper.save(smsInfo);
//
//        SmsInfo smsInfo2 = new SmsInfo();
//        smsInfo2.sender = "我";
//        smsInfo2.datetime = Utils.getDate(calendar);
//        smsInfo2.type = 1;
//        smsInfo2.content = "诈骗信息测试";
//        mDBHelper.save(smsInfo2);
//        chatState = (AppViewModel.ChatState) getIntent().getSerializableExtra("chat_state");

    }

    /**
     * Toggle selection for all items.
     *
     * @param isChecked If true, select all items; if false, deselect all.
     */
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
    public void onClick(View view) {
        int vid = view.getId();
        if(vid == R.id.ic_back){
            if(mSmsAdapter.getEditMode()) {
                mSmsAdapter.setEditMode(false);
                long_click_interface.setVisibility(View.GONE);
            } else finish();
        } else if (vid == R.id.btn_clear) {
            // 清空按钮
            if (mSmsAdapter.getEditMode()) {
                // 如果处于编辑模式，删除选中的项
                deleteSelectedItems();
            } else {
                // 如果不在编辑模式，弹出确认对话框清空所有记录
                showClearAllDialog();
            }
        }
    }

    /**
     * 显示清空所有记录的确认对话框
     */
    private void showClearAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
        builder.setMessage("确定要清空所有信息记录？");
        builder.setPositiveButton("是", (dialog, which) -> {
            mDBHelper.deleteAllSmsInfo(); // 清空数据库
            mSmsList.clear(); // 清空列表
            refreshTotalNum(); // 刷新总记录数
            mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
            ToastUtil.show(this, "历史信息已清空");
        });
        builder.setNegativeButton("否", null);
        builder.create().show();
    }

    /**
     * 删除选中的项
     */
    private void deleteSelectedItems() {
        List<SmsInfo> selectedItems = mSmsAdapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            ToastUtil.show(this, "请先选择要删除的项！");
            return;
        }

        // 删除选中的项
        for (SmsInfo info : selectedItems) {
            mDBHelper.deleteSmsInfoById(info.id); // 从数据库删除
            mSmsList.remove(info); // 从列表删除
        }

        mSmsAdapter.notifyDataSetChanged(); // 刷新适配器
        refreshTotalNum(); // 刷新总记录数
        ToastUtil.show(this, "已删除选中的信息！");

        // 退出编辑模式
        mSmsAdapter.setEditMode(false);
        long_click_interface.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        showSMS();
    }

    private void showSMS() {
        mSmsList = mDBHelper.queryAllSmsInfo();
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
        refreshTotalNum();
    }

    private void refreshTotalNum() {
        tv_total_num.setText(String.valueOf(mSmsList.size()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(HistoryActivity.this, SmsDetailActivity.class);
        intent.putExtra("sms_id", mSmsList.get(position).id);
//        intent.putExtra("chat_state", chatState);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSmsAdapter.setEditMode(true);
        long_click_interface.setVisibility(View.VISIBLE);
//        checkBox_select_all.setVisibility(View.VISIBLE);
//        SmsInfo info = mSmsList.get(position);
//        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
//        builder.setMessage("是否删除此条信息？");
//        builder.setPositiveButton("是", (dialog, which) -> {
//            // 删除该商品
//            mDBHelper.deleteSmsInfoById(info.id);
//            mSmsList.remove(position);
//            // 通知适配器发生了数据变化
//            mSmsAdapter.notifyDataSetChanged();
//            // 刷新总数
//            refreshTotalNum();
//            ToastUtil.show(this, "已删除该信息！");
//        });
//        builder.setNegativeButton("否", null);
//        builder.create().show();
        return true;
    }
}