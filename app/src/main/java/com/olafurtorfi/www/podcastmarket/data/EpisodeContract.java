package com.olafurtorfi.www.podcastmarket.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class EpisodeContract {
    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.olafurtorfi.www.podcastmarket";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.olafurtorfi.www.episode/episode/
     *     [           BASE_CONTENT_URI         ][ PATH_EPISODE ]
     *
     * is a valid path for looking at episode data.
     *
     *      content://com.olafurtorfi.www.episode/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_EPISODE = "episode";
    public static final String PATH_PODCAST = "podcast";
    public static final String PATH_ID = "id";

    /* Inner class that defines the table contents of the episode table */
    public static final class EpisodeEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Episode table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EPISODE)
                .build();

        /* Used internally as the name of our episode table. */
        public static final String TABLE_NAME = "episode";

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular episode row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load episode data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PODCAST = "podcast";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FILE_PATH = "path";
        public static final String COLUMN_FILE_URL = "url";

        /**
         * Builds a URI that adds the episode date to the end of the episode content URI path.
         * This is used to query details about a single episode entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param podcastId Normalized date in milliseconds
         * @return Uri to query details about a single episode entry
         */
        public static Uri buildEpisodeUriWithPodcast(long podcastId) {
            Log.v("EpisodeContract", "Podcast Id: " + podcastId);
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PODCAST)
                    .appendPath(String.valueOf(podcastId))
                    .build();
        }
        public static Uri buildEpisodeUriWithId(long id) {
            Log.v("EpisodeContract", "Podcast Id: " + id);
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_ID)
                    .appendPath(String.valueOf(id))
                    .build();
        }
    }
}
