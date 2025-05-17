package com.example.smsdetection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smsdetection.R;
import com.example.smsdetection.entity.SmsInfo;

import java.util.List;
//fwk
import android.app.Activity;
import java.util.ArrayList;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;

//fwk
public class SmsAdapter extends BaseAdapter {

    private Context mContext;
    private List<SmsInfo> mSmsList;

    private boolean isEditMode = false;

    public SmsAdapter(Context mContext, List<SmsInfo> mSmsList) {
        this.mContext = mContext;
        this.mSmsList = mSmsList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged();// 刷新列表
    }

    public boolean getEditMode() {
        return isEditMode;
    }

    @Override
    public int getCount() {
        return mSmsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSmsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // 获取布局文件item_cart.xml的根视图
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sms, null);
//            fwk
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            holder.item_datetime = convertView.findViewById(R.id.item_datetime);
//            fwk
            holder.item_date = convertView.findViewById(R.id.item_date);
            holder.item_time = convertView.findViewById(R.id.item_time);
            holder.item_sender = convertView.findViewById(R.id.item_sender);
            holder.item_content = convertView.findViewById(R.id.item_content);
            holder.item_type = convertView.findViewById(R.id.item_type);
            holder.ic_warning = convertView.findViewById(R.id.ic_warning);
            holder.card = convertView.findViewById(R.id.card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SmsInfo info = mSmsList.get(position);
        String[] strs = info.datetime.split("=");
        holder.item_date.setText(strs[0]);
        holder.item_time.setText(strs[1]);
        holder.item_sender.setText(info.sender.length() < 14? info.sender:info.sender.substring(0,14)+"……");
        holder.item_content.setText(info.content.length() < 46? info.content:info.content.substring(0,46)+"……");
        holder.item_type.setText(info.type==1?"诈骗":"普通");
        holder.ic_warning.setVisibility(info.type==1?View.VISIBLE:View.GONE);
        holder.item_type.setTextColor(info.type==1?convertView.getResources().getColor(R.color.red):convertView.getResources().getColor(R.color.green));
//fwk
        // 根据编辑模式显示或隐藏CheckBox
//        if (isEditMode) {
//            holder.checkBox.setVisibility(View.VISIBLE);
//            holder.checkBox.setChecked(info.isSelected);
//        } else {
//            holder.checkBox.setVisibility(View.GONE);
//        }

        // 设置选中状态对应的背景
        if (info.isSelected) {
            holder.card.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.glass_green_border_card)
            );
        } else {
            holder.card.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.glass_card)
            );
        }

        // 处理CheckBox的点击事件
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            info.isSelected = isChecked;
//
//            // Update the "Select All" CheckBox in the activity
//            updateSelectAllCheckBoxState();
//        });

//        // 点击整个item来选中或取消选中
//        holder.card.setOnClickListener(v -> {
//            // 切换选中状态
//            info.isSelected = !info.isSelected;
//
//            // 更新UI
//            if (info.isSelected) {
//                holder.card.setBackground(
//                        ContextCompat.getDrawable(mContext, R.drawable.glass_green_border_card)
//                );
//            } else {
//                holder.card.setBackground(
//                        ContextCompat.getDrawable(mContext, R.drawable.glass_card)
//                );
//            }
//
//            // 更新“全选”CheckBox状态
//            updateSelectAllCheckBoxState();
//        });

//fwk
        return convertView;
    }
//    fwk
    /**
            * 更新选择所有按钮
     */
    public void updateSelectAllCheckBoxState() {
        CheckBox checkBoxSelectAll = ((Activity) mContext).findViewById(R.id.select_all);
        if (checkBoxSelectAll != null) {
            boolean allSelected = areAllItemsSelected();
            checkBoxSelectAll.setChecked(allSelected);
        }
    }

    /**
     * 检查是否所有项都被选中
     */
    private boolean areAllItemsSelected() {
        if (mSmsList.isEmpty()) return false;
        for (SmsInfo info : mSmsList) {
            if (!info.isSelected) return false;
        }
        return true;
    }

    // 获取选中的项
    public List<SmsInfo> getSelectedItems() {
        List<SmsInfo> selectedItems = new ArrayList<>();
        for (SmsInfo info : mSmsList) {
            if (info.isSelected) {
                selectedItems.add(info);
            }
        }
        return selectedItems;
    }
//    fwk

    public static final class ViewHolder {
        public TextView item_date;
        public TextView item_time;
        public TextView item_datetime;
        public TextView item_sender;
        public TextView item_content;
        public TextView item_type;
        public ImageView ic_warning;
        public CheckBox checkBox;
        public LinearLayout card;
    }
}
