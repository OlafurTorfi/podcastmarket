package com.olafurtorfi.www.podcastmarket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "podcasts.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
     * with the Android course for a while now. Believe it or not, older versions of Sunshine
     * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
     * versions of Sunshine could cause everything to break. Although that is certainly a rare
     * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
     * version your databases.
     */
    private static final int DATABASE_VERSION = 3;

    public PodcastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our podcast data.
         */
        final String SQL_CREATE_PODCAST_TABLE =

                "CREATE TABLE " + PodcastContract.PodcastEntry.TABLE_NAME + " (" +

                /*
                 * PodcastEntry did not explicitly declare a column called "_ID". However,
                 * PodcastEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        PodcastContract.PodcastEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        PodcastContract.PodcastEntry.COLUMN_TITLE   + " TEXT NOT NULL, "                    +
                        PodcastContract.PodcastEntry.COLUMN_DESCRIPTION   + " TEXT NOT NULL, "                    +
                        PodcastContract.PodcastEntry.COLUMN_AUTHOR   + " TEXT NOT NULL, "                   +

                /*
                 * To ensure this table can only contain one podcast entry per date, we declare
                 * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a podcast entry for a certain date and we attempt to
                 * insert another podcast entry with that date, we replace the old podcast entry.
                 */
                        " UNIQUE (" + PodcastContract.PodcastEntry.COLUMN_TITLE + ", "+PodcastContract.PodcastEntry.COLUMN_AUTHOR+") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */

        final String SQL_CREATE_EPISODE_TABLE =
                "CREATE TABLE " + EpisodeContract.EpisodeEntry.TABLE_NAME + " (" +
                        EpisodeContract.EpisodeEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EpisodeContract.EpisodeEntry.COLUMN_TITLE   + " TEXT NOT NULL, "                    +
                        EpisodeContract.EpisodeEntry.COLUMN_DESCRIPTION   + " TEXT NOT NULL, "                    +
                        EpisodeContract.EpisodeEntry.COLUMN_PODCAST   + " INTEGER NOT NULL, "                   +
                        EpisodeContract.EpisodeEntry.COLUMN_DATE   + " INTEGER, "                   +
                        EpisodeContract.EpisodeEntry.COLUMN_FILE_PATH + " TEXT, "                   +
                        EpisodeContract.EpisodeEntry.COLUMN_FILE_URL + " TEXT NOT NULL, "                   +
                        " UNIQUE (" + EpisodeContract.EpisodeEntry.COLUMN_TITLE + ", "+EpisodeContract.EpisodeEntry.COLUMN_PODCAST+") ON CONFLICT REPLACE);";
        Log.d("PodcastDbHelper", "Episode string " + SQL_CREATE_EPISODE_TABLE);

        Log.d("PodcastDbHelper","about to execute create table sql for podcast");
        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_TABLE);
        Log.d("PodcastDbHelper","about to execute create table sql for episodes");
        sqLiteDatabase.execSQL(SQL_CREATE_EPISODE_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodcastContract.PodcastEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EpisodeContract.EpisodeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}