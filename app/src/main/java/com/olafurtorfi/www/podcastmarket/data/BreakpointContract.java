package com.olafurtorfi.www.podcastmarket.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class BreakpointContract {

    public static final String CONTENT_AUTHORITY = "com.olafurtorfi.www.podcastmarket";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BREAKPOINT = "breakpoint";
    public static final String PATH_EPISODE = "episode";
    public static final String PATH_PODCAST = "podcast";

    /* Inner class that defines the table contents of the breakpoint table */
    public static final class BreakpointEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Breakpoint table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_BREAKPOINT)
                .build();

        /* Used internally as the name of our breakpoint table. */
        public static final String TABLE_NAME = "breakpoint";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_START = "start";
        public static final String COLUMN_PODCAST = "podcast";
        public static final String COLUMN_EPISODE = "episode";
        public static final String COLUMN_TIME = "time";

        public static Uri buildBreakpointUriWithPodcastAndEpisodeTitles(String podcast, String episode) {
            Log.v("BreakpointContract", "Podcast Title: " + podcast + ", Episode Title: " + episode);
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PODCAST)
                    .appendPath(podcast)
                    .appendPath(PATH_EPISODE)
                    .appendPath(episode)
                    .build();
        }
    }
}
