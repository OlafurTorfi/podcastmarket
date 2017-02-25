package com.olafurtorfi.www.podcastmarket.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastContract {
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
     *     content://com.olafurtorfi.www.podcast/podcast/
     *     [           BASE_CONTENT_URI         ][ PATH_PODCAST ]
     *
     * is a valid path for looking at podcast data.
     *
     *      content://com.olafurtorfi.www.podcast/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_PODCAST = "podcast";
    public static final String PATH_TITLE = "title";
    public static final String PATH_ID = "id";

    /* Inner class that defines the table contents of the podcast table */
    public static final class PodcastEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Podcast table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PODCAST)
                .build();

        /* Used internally as the name of our podcast table. */
        public static final String TABLE_NAME = "podcast";

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular podcast row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load podcast data at 9:00 PM on September 23, 2016,
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
        public static final String COLUMN_AUTHOR = "author";


        /**
         * Builds a URI that adds the podcast date to the end of the podcast content URI path.
         * This is used to query details about a single podcast entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param title Normalized date in milliseconds
         * @return Uri to query details about a single podcast entry
         */
        public static Uri buildPodcastUriWithTitle(String title) {
            Uri build = CONTENT_URI.buildUpon()
                    .appendPath(PATH_TITLE)
                    .appendPath(title)
                    .build();
            Log.v("PodcastContract", "buildPodcastUriWithTitle: " + build.toString());
            return build;
        }
        public static Uri buildPodcastUriWithId(long id) {
            Uri build = CONTENT_URI.buildUpon()
                    .appendPath(PATH_ID)
                    .appendPath(String.valueOf(id))
                    .build();
            Log.v("PodcastContract", "buildPodcastUriWithId: " + build.toString());
            return build;
        }
    }
}
