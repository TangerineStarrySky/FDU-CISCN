package com.example.smsdetection.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smsdetection.R;
import com.example.smsdetection.VideoLearningActivity;
import com.example.smsdetection.VideoPlayerActivity;
import com.example.smsdetection.entity.Video;

import java.io.IOException;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {
    private List<Video> videoList;
    private Context context;

    public VideoListAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
            holder = new ViewHolder();
            holder.videoTitle = convertView.findViewById(R.id.videoTitle);
            holder.thumbnail = convertView.findViewById(R.id.thumbnail);
            holder.duration = convertView.findViewById(R.id.duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Video video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.duration.setText("10:00");
        Glide.with(context).load(video.getThumbnailUrl()).into(holder.thumbnail);

        // 点击播放视频
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", video.getUrl());
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView videoTitle;
        ImageView thumbnail;
        TextView duration;
    }

//    public static String getVideoDuration(String videoPath) throws IOException {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        try {
//            retriever.setDataSource(videoPath); // 可以是本地路径或网络URL
//            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            long timeInMillisec = Long.parseLong(time);
//            long minutes = (timeInMillisec / 1000) / 60;
//            long seconds = (timeInMillisec / 1000) % 60;
//
//            return String.format("%02d:%02d", minutes, seconds);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "00:00"; // 获取失败返回默认值
//        } finally {
//            retriever.release();
//        }
//    }
}
