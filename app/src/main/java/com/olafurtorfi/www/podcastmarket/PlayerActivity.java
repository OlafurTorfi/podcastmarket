package com.olafurtorfi.www.podcastmarket;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.olafurtorfi.www.podcastmarket.data.EpisodeObject;
import com.olafurtorfi.www.podcastmarket.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity{

    ActivityPlayerBinding mBinding;

    private EpisodeObject episode;

    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton startJumpButton;
    private ImageButton stopJumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_player);
        playButton = (ImageButton) findViewById(R.id.play);
        pauseButton = (ImageButton) findViewById(R.id.pause);
        startJumpButton = (ImageButton) findViewById(R.id.startJump);
        stopJumpButton = (ImageButton) findViewById(R.id.stopJump);
        pauseButton.setVisibility(View.INVISIBLE);
        stopJumpButton.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String filePath = intent.getStringExtra("filePath");
        String podcast = intent.getStringExtra("podcast");
        String fileUrl = intent.getStringExtra("fileUrl");
        episode = new EpisodeObject(title,description,podcast,filePath,fileUrl);
        displayEpisodeInfo(episode);
    }

    private void displayEpisodeInfo(EpisodeObject episode){
        mBinding.title.setText(episode.title);
        mBinding.description.setText(episode.description);
    }

    public void play(View view){
        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
    }

    public void pause(View view){
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
    }

    public void startJump(View view){
        startJumpButton.setVisibility(View.INVISIBLE);
        stopJumpButton.setVisibility(View.VISIBLE);
    }

    public void stopJump(View view){
        startJumpButton.setVisibility(View.VISIBLE);
        stopJumpButton.setVisibility(View.INVISIBLE);
    }
}
