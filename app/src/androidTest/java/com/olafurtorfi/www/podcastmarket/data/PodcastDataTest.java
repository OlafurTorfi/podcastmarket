package com.olafurtorfi.www.podcastmarket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.olafurtorfi.www.podcastmarket.utilities.FakeDataUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

/**
 * Created by olitorfi on 20/02/2017.
 */
@RunWith(AndroidJUnit4.class)

public class PodcastDataTest {

    private Context context;


    private SQLiteDatabase database;
    private PodcastDbHelper dbHelper;

    @Before
    public void before(){
        Log.i("Podcast Data Test", "Setting up database");
        context = InstrumentationRegistry.getTargetContext();
        dbHelper = new PodcastDbHelper(context);
        database = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(database,0,1);
    }

    @Test
    public void testQueryFakeData(){

        FakeDataUtil.insertFakeEpisodes(context);
        FakeDataUtil.insertFakePodcasts(context);
        database = dbHelper.getReadableDatabase();

        Log.i("Podcast Data Test", "Running query on fake data");
        Cursor query = database.query(PodcastContract.PodcastEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        Log.i("Test Query", "number of rows" + query.getCount());
        assertTrue("query has some rows",query.getCount()>0);
        query.close();


        Cursor query2 = database.query(EpisodeContract.EpisodeEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        Log.i("Test Query2", "number of rows" + query2.getCount());
        assertTrue("query has some rows",query2.getCount()>0);
        query2.close();
    }

    @Test
    public void testDuplicateDateInsertBehaviorShouldReplace() {

        dbHelper = new PodcastDbHelper(context);
        Log.i("Podcast Data Test", "Running duplicate insert test");
        database = dbHelper.getWritableDatabase();
        /* Obtain podcast values from TestUtilities */
        ContentValues testPodcastValues = new ContentValues();
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_TITLE,"Testkast");
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_AUTHOR,"Testcaster");
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_DESCRIPTION,"This is test");
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_ID,123);


        /*
         * Get the original podcast ID of the testPodcastValues to ensure we use a different
         * podcast ID for our next insert.
         */
        long originalPodcastId = testPodcastValues.getAsLong(PodcastContract.PodcastEntry.COLUMN_ID);

        /* Insert the ContentValues with old podcast ID into database */
        database.insert(
                PodcastContract.PodcastEntry.TABLE_NAME,
                null,
                testPodcastValues);

        /*
         * We don't really care what this ID is, just that it is different than the original and
         * that we can use it to verify our "new" podcast entry has been made.
         */
        long newPodcastId = originalPodcastId + 1;
        Log.i("PodcastDataTest","id... " + newPodcastId);

        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_ID, newPodcastId);

        /* Insert the ContentValues with new podcast ID into database */
        database.insert(
                PodcastContract.PodcastEntry.TABLE_NAME,
                null,
                testPodcastValues);

        /* Query for a podcast record with our new podcast ID */
        Cursor queryCursor = database.query(
                PodcastContract.PodcastEntry.TABLE_NAME,
                new String[]{PodcastContract.PodcastEntry.COLUMN_ID},
                null,
                null,
                null,
                null,
                null);

        String recordWithNewIdNotFound =
                "New record did not overwrite the previous record for the same Title and Author.";
        Log.i("PodcastDataTest","Cursor count " + queryCursor.getCount());
        assertTrue(recordWithNewIdNotFound,
                queryCursor.getCount() == 1);

        /* Always close the cursor after you're done with it */
        queryCursor.close();
    }
}
