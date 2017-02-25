package com.olafurtorfi.www.podcastmarket.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastProvider  extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int CODE_PODCAST = 100;
    public static final int CODE_PODCAST_WITH_TITLE = 101;
    public static final int CODE_EPISODE = 102;
    public static final int CODE_EPISODE_OF_PODCAST = 103;
    public static final int CODE_EPISODE_OF_ID = 104;
    public static final int CODE_PODCAST_OF_ID = 105;


    private static String TAG = "PodcastProvider";
    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of PodcastProvider and is a
     * common convention in Android programming.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PodcastDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_PODCAST and
     * CODE_PODCAST_WITH_DATE constants defined above.
     * <p>
     * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
     * expressions instead? After all, we really just need to match some patterns, and we can
     * use regular expressions to do that right?" Because you're not crazy, that's why.
     * <p>
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automagically. Remember, the best programmers try
     * to never reinvent the wheel. If there is a solution for a problem that exists and has
     * been tested and proven, you should almost always use it unless there is a compelling
     * reason not to.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_PODCAST and CODE_PODCAST_WITH_TITLE
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PodcastContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Sunshine, we use CODE_PODCAST or CODE_PODCAST_WITH_TITLE.
         */

        /* This URI is content://com.olafurtorfi.www.podcast/podcast/ */
        matcher.addURI(authority, PodcastContract.PATH_PODCAST, CODE_PODCAST);
        matcher.addURI(authority, EpisodeContract.PATH_EPISODE, CODE_EPISODE);

        /*
         * This URI would look something like content://com.olafurtorfi.www.podcast/podcast/Torfakast
         * The "/#" signifies to the UriMatcher that if PATH_PODCAST is followed by ANY number,
         * that it should return the CODE_PODCAST_WITH_DATE code
         */
        matcher.addURI(authority, PodcastContract.PATH_PODCAST + "/" + PodcastContract.PATH_TITLE + "/*", CODE_PODCAST_WITH_TITLE);
        matcher.addURI(authority, EpisodeContract.PATH_EPISODE + "/" + EpisodeContract.PATH_PODCAST + "/#", CODE_EPISODE_OF_PODCAST);

        matcher.addURI(authority, PodcastContract.PATH_PODCAST + "/" + PodcastContract.PATH_ID + "/#", CODE_PODCAST_OF_ID);
        matcher.addURI(authority, EpisodeContract.PATH_EPISODE + "/" + EpisodeContract.PATH_ID + "/#", CODE_EPISODE_OF_ID);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     *
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since PodcastDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        Log.v("PodcastProvider","Creating podcast provider");
        mOpenHelper = new PodcastDbHelper(getContext());
        return true;
    }

    /**
     * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
     * inserting multiple rows of data at a time from a podcast podcast. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {

            case CODE_PODCAST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if(value != null){
                            long _id = db.insert(PodcastContract.PodcastEntry.TABLE_NAME, null, value);
                            if (_id != -1) {

                                Log.v(TAG, "bulkInsert: " + value.toString() + " and id: " + _id);
                                rowsInserted++;
                            }
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_EPISODE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values){
                        if(value != null){
                            Log.v(TAG, "bulkInsert: " + value.toString());
                            long _id = db.insert(EpisodeContract.EpisodeEntry.TABLE_NAME,null,value);
                            if (_id != -1){
                                rowsInserted++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);


        }
    }

    /**
     * Handles query requests from clients. We will use this method in Sunshine to query for all
     * of our podcast data as well as to query for the podcast on a particular day.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {
            case CODE_PODCAST_WITH_TITLE: {
                String title = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionArguments = new String[]{title};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        PodcastContract.PodcastEntry.TABLE_NAME,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        /*
                         * The URI that matches CODE_PODCAST_WITH_DATE contains a date at the end
                         * of it. We extract that date and use it with these next two lines to
                         * specify the row of podcast we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        PodcastContract.PodcastEntry.COLUMN_TITLE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_PODCAST_OF_ID: {
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        PodcastContract.PodcastEntry.TABLE_NAME,
                        projection,
                        PodcastContract.PodcastEntry.COLUMN_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_PODCAST: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        PodcastContract.PodcastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_EPISODE:{
                cursor = mOpenHelper.getReadableDatabase().query(
                        EpisodeContract.EpisodeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CODE_EPISODE_OF_PODCAST: {
                String podcastTitle = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{podcastTitle};
                cursor = mOpenHelper.getReadableDatabase().query(
                        EpisodeContract.EpisodeEntry.TABLE_NAME,
                        projection,
                        EpisodeContract.EpisodeEntry.COLUMN_PODCAST + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CODE_EPISODE_OF_ID: {
                String podcastId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{podcastId};
                cursor = mOpenHelper.getReadableDatabase().query(
                        EpisodeContract.EpisodeEntry.TABLE_NAME,
                        projection,
                        EpisodeContract.EpisodeEntry.COLUMN_ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_PODCAST:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        PodcastContract.PodcastEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case CODE_EPISODE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        EpisodeContract.EpisodeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    /**
     * In Sunshine, we aren't going to do anything with this method. However, we are required to
     * override it as PodcastProvider extends ContentProvider and getType is an abstract method in
     * ContentProvider. Normally, this method handles requests for the MIME type of the data at the
     * given URI. For example, if your app provided images at a particular URI, then you would
     * return an image URI from this method.
     *
     * @param uri the URI to query.
     * @return nothing in Sunshine, but normally a MIME type string, or null if there is no type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("GetType is not implemented.");
    }

    /**
     * In Sunshine, we aren't going to do anything with this method. However, we are required to
     * override it as PodcastProvider extends ContentProvider and insert is an abstract method in
     * ContentProvider. Rather than the single insert method, we are only going to implement
     * {@link PodcastProvider#bulkInsert}.
     *
     * @param uri    The URI of the insertion request. This must not be null.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be null
     * @return nothing in Sunshine, but normally the URI for the newly inserted item.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long resultId;
        Uri uriOut = null;
        switch(sUriMatcher.match(uri)){
            case CODE_PODCAST:
                Log.v(TAG, "insert podcast: " + uri.toString());
                db.beginTransaction();
                try {
                    resultId = db.insert(PodcastContract.PodcastEntry.TABLE_NAME, null, values);
                    if (resultId != -1) {
                        Log.v(TAG, "inserted with id: " + resultId);
                        uriOut = PodcastContract.PodcastEntry.buildPodcastUriWithId(resultId);
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                    db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                return uriOut;
            case CODE_EPISODE:
                Log.v(TAG, "insert episode: " + uri.toString());
                db.beginTransaction();
                try {
                    resultId = db.insert(EpisodeContract.EpisodeEntry.TABLE_NAME, null, values);
                    if (resultId != -1) {
                        Log.v(TAG, "inserted with id: " + resultId);
                        uriOut = EpisodeContract.EpisodeEntry.buildEpisodeUriWithId(resultId);
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return uriOut;
            default:
                Log.e(TAG, "insert: should not have reached its default method");
                throw new RuntimeException("Method for following uri not implemented " + uri.toString());
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Update not implemented");
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}