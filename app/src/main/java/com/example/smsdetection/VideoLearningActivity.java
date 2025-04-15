package com.example.smsdetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VideoLearningActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private VideoListAdapter adapter;
    private List<Video> videoList = new ArrayList<>();
    private TextView title;
    private TextView history;
    private TabLayout tabLayout;

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
        findViewById(R.id.ic_back).setOnClickListener(this);

        tabLayout = findViewById(R.id.tabLayout);

        // 设置默认选中 "反诈视频"（第二个 Tab，索引从 0 开始）
        TabLayout.Tab defaultTab = tabLayout.getTabAt(1); // 1 表示第二个 Tab
        if (defaultTab != null) {
            defaultTab.select();
        }

        // 监听 Tab 切换
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // 第2个 Tab（索引从 0 开始）
                    Intent intent = new Intent(VideoLearningActivity.this, LearningActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 这里可以添加“重复点击”的逻辑
            }
        });

        listView = findViewById(R.id.videoList);

        // 模拟视频数据
        videoList.add(new Video("反诈宣传视频 1", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://upos-sz-mirroraliov.bilivideo.com/upgcxcode/07/02/1631280207/1631280207-1-192.mp4?e=ig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&os=aliovbv&og=cos&nbs=1&oi=760753016&gen=playurlv3&platform=pc&trid=349a3357fb3f439c9c603f40829e605u&mid=0&deadline=1743935256&tag=&uipk=5&upsig=0e38aee177054fb71a3820c0e4daaac5&uparams=e,os,og,nbs,oi,gen,platform,trid,mid,deadline,tag,uipk&bvc=vod&nettype=0&bw=544515&build=0&dl=0&f=u_0_0&agrr=1&buvid=&orderid=0,2"));
        videoList.add(new Video("反诈宣传视频 2", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 1", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 2", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 1", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 2", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));
        videoList.add(new Video("反诈宣传视频 3", "http://i2.hdslb.com/bfs/storyff/n240729sa28nuxy0wcuvbr3ngzce6xgf_firsti.jpg", "https://www.w3schools.com/html/mov_bbb.mp4"));

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
