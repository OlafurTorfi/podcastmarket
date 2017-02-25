package com.olafurtorfi.www.podcastmarket.utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.olafurtorfi.www.podcastmarket.sync.PodcastSyncIntentService;

/**
 * Created by olitorfi on 22/02/2017.
 */

public class RealDataUtil {
    private static String TAG = "Real Data Util";
    static String[] rssFeeds = {
            "https://feeds.feedburner.com/dancarlin/history?format=xml",
            "https://feeds.feedburner.com/i-ljosi-sogunnar"
    };
    public static void insertRealTestData(Context context){
        Log.d(TAG, "insertRealTestData: " + rssFeeds[1]);
        for(String str : rssFeeds){
            Intent intent = new Intent(context, PodcastSyncIntentService.class);
            intent.putExtra("urlString", str);
            context.startService(intent);
        }
    }

}
