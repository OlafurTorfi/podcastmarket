package com.olafurtorfi.www.podcastmarket.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastSyncIntentService  extends IntentService {

    private String TAG = "PodcastSyncIntentServic";
    public PodcastSyncIntentService() {
        super("PodcastSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlString = intent.getStringExtra("urlString");
        Log.d(TAG, "onHandleIntent: " + urlString);
        if(urlString != null){
            PodcastSyncTask.syncPodcast(this, urlString);
        }
    }
}
