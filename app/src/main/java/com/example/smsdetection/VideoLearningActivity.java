package com.example.smsdetection;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smsdetection.adapter.VideoListAdapter;
import com.example.smsdetection.entity.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import android.net.Uri;
import com.google.android.exoplayer2.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class VideoLearningActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private VideoListAdapter adapter;
    private List<Video> videoList = new ArrayList<>();
    private TextView title;
    private TextView history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_learning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.tv_title);
        title.setText("视频学习");
        history = findViewById(R.id.tv_history);
        history.setVisibility(View.GONE);

        listView = findViewById(R.id.videoList);

        // 模拟视频数据
        videoList.add(new Video("反诈宣传视频 1", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 2", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 1", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 2", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 1", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 2", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "https://www.w3schools.com/html/pic_trulli.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));

        // 绑定适配器
        adapter = new VideoListAdapter(videoList, this);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid == R.id.ic_back) {
            finish();
        }
    }
}
