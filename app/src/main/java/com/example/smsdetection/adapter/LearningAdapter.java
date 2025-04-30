package com.example.smsdetection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smsdetection.R;
import com.example.smsdetection.entity.ArticleLearning;

import java.util.List;

public class LearningAdapter extends BaseAdapter {
    private Context context;
    List<ArticleLearning> articleLearningList;

    public LearningAdapter(Context context, List<ArticleLearning> articleLearningList) {
        this.context = context;
        this.articleLearningList = articleLearningList;
    }

    @Override
    public int getCount() {
        return articleLearningList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleLearningList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 重点：生成每一行的界面
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 提升性能：复用老的View
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.learning_item, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvSummary = convertView.findViewById(R.id.tv_summary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置数据
        holder.tvTitle.setText(articleLearningList.get(position).getTitle());
        holder.tvSummary.setText(articleLearningList.get(position).getSummary());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvSummary;
    }
}
