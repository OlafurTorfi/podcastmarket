package com.olafurtorfi.www.podcastmarket.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.olafurtorfi.www.podcastmarket.R;
import com.olafurtorfi.www.podcastmarket.data.BreakpointContract;
import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.EpisodeObject;
import com.olafurtorfi.www.podcastmarket.databinding.ActivityPlayerBinding;
import com.olafurtorfi.www.podcastmarket.utilities.BreakpointTimerTask;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity{

    ActivityPlayerBinding mBinding;

    private EpisodeObject episode;

    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton startJumpButton;
    private ImageButton stopJumpButton;

    private static final String TAG = "PlayerActivity";
    private MediaPlayer mPlayer;
    private Cursor breakpoints;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize databinding layout
        setContentView(R.layout.activity_player);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);

        //initialize view objects
        playButton = (ImageButton) findViewById(R.id.play);
        pauseButton = (ImageButton) findViewById(R.id.pause);
        startJumpButton = (ImageButton) findViewById(R.id.startJump);
        stopJumpButton = (ImageButton) findViewById(R.id.stopJump);
        pauseButton.setVisibility(View.INVISIBLE);
        stopJumpButton.setVisibility(View.INVISIBLE);

        //fetch data associated with intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String filePath = intent.getStringExtra("filePath");
        String podcast = intent.getStringExtra("podcast");
        String fileUrl = intent.getStringExtra("fileUrl");
        String author = intent.getStringExtra("author");

        episode = new EpisodeObject(title, description, podcast, filePath, fileUrl, author);

        //bind data to the view
        displayEpisodeInfo(episode);



        //initialize media player
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "onError: " + mp.toString() + ", what:" + what + ", extra:" + extra);
                return false;
            }
        });
        if(filePath != null){
            try {
                AssetFileDescriptor descriptor = getAssets().openFd(filePath);
                long start = descriptor.getStartOffset();
                long end = descriptor.getLength();
                mPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);
                mPlayer.setVolume(1.0f, 1.0f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // get breakpoints
        breakpoints = getContentResolver()
                .query(BreakpointContract.BreakpointEntry
                        .buildBreakpointUriWithPodcastAndEpisodeTitles(episode.podcast, episode.title), null, null, null, null);

        if(breakpoints.getCount()>0){
            timer = new Timer();

            // debug stuff
            for (int i = 0; i < breakpoints.getCount(); i++){
                breakpoints.moveToPosition(i);
                Log.d(TAG, "breakpoint at : " + breakpoints.getLong(breakpoints.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_TIME)) +
                " of type : " + breakpoints.getInt(breakpoints.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_START)));
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent episodeIntent = new Intent(this, EpisodeActivity.class);
            Uri uriForPodcastClicked = EpisodeContract.EpisodeEntry.buildEpisodeUriWithPodcast(episode.podcast);
            episodeIntent.setData(uriForPodcastClicked);
            startActivity(episodeIntent);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.list, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    private void scheduleBreaks(){
        int currentPosition = mPlayer.getCurrentPosition();
        long time = breakpoints.getLong(breakpoints.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_TIME));
        int start = breakpoints.getInt(breakpoints.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_START));

        if((int) time > currentPosition){
            if(start != 1){
                if (start == 0){
                    mPlayer.seekTo((int) time);
                    if(breakpoints.moveToNext()){
                        scheduleBreaks();
                    }
                }
            } else {
                if(breakpoints.moveToNext()){
                    long nextEnd = breakpoints.getLong(breakpoints.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_TIME));
                    Log.i(TAG, "creating breakpoint at : " + time + " going to " + nextEnd);
                    timer.schedule(new BreakpointTimerTask(nextEnd) {
                        @Override
                        public void run() {
                            long theEnd = this.getEnd();
                            Log.i(TAG, "breakpoint reached, going from " + mPlayer.getCurrentPosition() + " to " + theEnd);
                            mPlayer.seekTo((int) theEnd);
                        }
                    },time - currentPosition);

                    if(breakpoints.moveToNext()){
                        scheduleBreaks();
                    }
                } else {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.i(TAG, "reached last breakpoint at: " + mPlayer.getCurrentPosition());
                            mPlayer.stop();
                        }
                    }, time - currentPosition);
                }
            }
        } else if(breakpoints.moveToNext()){
            scheduleBreaks();
        }

    }
    private void displayEpisodeInfo(EpisodeObject episode){
        mBinding.title.setText(episode.title);
        mBinding.description.setText(episode.description);
    }

    public void play(View view){
        Log.d(TAG, "play: clicked" );
        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        if(!mPlayer.isLooping()){
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared: ");
                    if(breakpoints.getCount() > 0){
                        breakpoints.moveToFirst();
                        scheduleBreaks();
                    }
                    mp.start();
                }
            });
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void pause(View view){
        Log.d(TAG, "pause: clicked");
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        mPlayer.pause();
        Cursor cursor = getContentResolver().query(BreakpointContract.BreakpointEntry.buildBreakpointUriWithPodcastAndEpisodeTitles(episode.podcast, episode.title), null, null, null, null);
        Log.d(TAG, "AMOUNT OF BREAKPOINTS NOW AT " + cursor.getCount());
    }

    public void startJump(View view){
        startJumpButton.setVisibility(View.INVISIBLE);
        stopJumpButton.setVisibility(View.VISIBLE);
        getContentResolver().insert(BreakpointContract.BreakpointEntry.CONTENT_URI,makeJump(mPlayer.getCurrentPosition(), true));
    }

    public void stopJump(View view){
        startJumpButton.setVisibility(View.VISIBLE);
        stopJumpButton.setVisibility(View.INVISIBLE);
        getContentResolver().insert(BreakpointContract.BreakpointEntry.CONTENT_URI,makeJump(mPlayer.getCurrentPosition(), false));
    }

    private ContentValues makeJump(int time, boolean start){
        ContentValues cv = new ContentValues();
        cv.put(BreakpointContract.BreakpointEntry.COLUMN_START,start);
        cv.put(BreakpointContract.BreakpointEntry.COLUMN_EPISODE, episode.title);
        cv.put(BreakpointContract.BreakpointEntry.COLUMN_PODCAST, episode.podcast);
        cv.put(BreakpointContract.BreakpointEntry.COLUMN_TYPE, "useremphasis");
        cv.put(BreakpointContract.BreakpointEntry.COLUMN_TIME, time);
        return cv;
    }
}
