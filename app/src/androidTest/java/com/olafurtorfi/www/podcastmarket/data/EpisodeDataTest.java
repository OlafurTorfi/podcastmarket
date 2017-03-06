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

public class EpisodeDataTest {

    private Context context = InstrumentationRegistry.getTargetContext();

    private SQLiteDatabase database;
    private PodcastDbHelper dbHelper = new PodcastDbHelper(context);

    private static final String TAG = "EpisodeDataTest";
    @Before
    public void before(){
        Log.i("Episode Data Test", "Setting up database");
        deleteAllRecordsFromTable();
        //dbHelper.onUpgrade(database,0,1);
    }

    @Test
    public void testQueryFakeData(){

        FakeDataUtil.insertFakeEpisodes(context);
        database = dbHelper.getReadableDatabase();

        Cursor query = database.query(EpisodeContract.EpisodeEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        Log.i("Test Query", "number of rows " + query.getCount());
        assertTrue("query has some rows",query.getCount() == 15);

        String title = "";
        for (int i = 0; i< query.getCount(); i++){
            query.moveToPosition(i);
            title = query.getString(query.getColumnIndex(EpisodeContract.EpisodeEntry.COLUMN_TITLE));
            Log.d(TAG, "testQueryFakeData: " + title);
        }

        query.close();
        database.close();
    }

    @Test
    public void testDuplicateDateInsertBehaviorShouldReplace() {


        database = dbHelper.getWritableDatabase();
        /* Obtain podcast values from TestUtilities */
        ContentValues testEpisodeValues = new ContentValues();
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_TITLE,"Testkast");
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_AUTHOR,"Tester");
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_PODCAST,12);
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_DESCRIPTION,"This is test");
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_FILE_URL,"someurlorother");
        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_ID,123);


        /*
         * Get the original podcast ID of the testEpisodeValues to ensure we use a different
         * podcast ID for our next insert.
         */
        long originalEpisodeId = testEpisodeValues.getAsLong(EpisodeContract.EpisodeEntry.COLUMN_ID);

        /* Insert the ContentValues with old podcast ID into database */
        database.insert(
                EpisodeContract.EpisodeEntry.TABLE_NAME,
                null,
                testEpisodeValues);

        /*
         * We don't really care what this ID is, just that it is different than the original and
         * that we can use it to verify our "new" podcast entry has been made.
         */
        long newEpisodeId = originalEpisodeId + 1;
        Log.i(TAG,"id... " + newEpisodeId);

        testEpisodeValues.put(EpisodeContract.EpisodeEntry.COLUMN_ID, newEpisodeId);

        /* Insert the ContentValues with new podcast ID into database */
        database.insert(
                EpisodeContract.EpisodeEntry.TABLE_NAME,
                null,
                testEpisodeValues);

        /* Query for a podcast record with our new podcast ID */
        Cursor queryCursor = database.query(
                EpisodeContract.EpisodeEntry.TABLE_NAME,
                new String[]{EpisodeContract.EpisodeEntry.COLUMN_ID},
                null,
                null,
                null,
                null,
                null);

        String recordWithNewIdNotFound =
                "New record did not overwrite the previous record for the same Title and Author.";
        Log.i("EpisodeDataTest","Cursor count " + queryCursor.getCount());
        assertTrue(recordWithNewIdNotFound,
                queryCursor.getCount() == 1);

        /* Always close the cursor after you're done with it */
        queryCursor.close();
        database.close();
    }
    private void deleteAllRecordsFromTable() {

        database = dbHelper.getWritableDatabase();
        /* The delete method deletes all of the desired rows from the table, not the table itself */
        database.delete(EpisodeContract.EpisodeEntry.TABLE_NAME, null, null);

        /* Always close the database when you're through with it */
        database.close();
    }
}
