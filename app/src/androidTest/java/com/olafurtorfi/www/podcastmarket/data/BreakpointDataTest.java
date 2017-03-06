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

public class BreakpointDataTest {


    private Context context = InstrumentationRegistry.getTargetContext();

    private SQLiteDatabase database;
    private PodcastDbHelper dbHelper = new PodcastDbHelper(context);

    @Before
    public void before(){
        Log.i("Breakpoint Data Test", "Setting up database");
        deleteAllRecordsFromTable();
    }

    @Test
    public void testQueryFakeData(){

        FakeDataUtil.insertFakeBreakpoints(context);

        database = dbHelper.getReadableDatabase();

        Log.i("Breakpoint Data Test", "Running query on fake data");
        Cursor cursor = database.query(BreakpointContract.BreakpointEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        Log.i("Test Query", "number of rows" + cursor.getCount());
        assertTrue("query has some rows",cursor.getCount()>0);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_PODCAST));
        String episodeTitle = cursor.getString(cursor.getColumnIndex(BreakpointContract.BreakpointEntry.COLUMN_EPISODE));
        cursor.close();
    }
    @Test
    public void testDuplicateDateInsertBehaviorShouldReplace() {

        dbHelper = new PodcastDbHelper(context);
        Log.i("Breakpoint Data Test", "Running duplicate insert test");
        database = dbHelper.getWritableDatabase();
        /* Obtain podcast values from TestUtilities */
        ContentValues testBreakpointValues = new ContentValues();
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_EPISODE,"Testkastepi");
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_PODCAST,"Testkat");
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_START,1);
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_TIME,123);
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_TYPE,"test");
        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_ID,1234);


        /*
         * Get the original podcast ID of the testBreakpointValues to ensure we use a different
         * podcast ID for our next insert.
         */
        long originalPodcastId = testBreakpointValues.getAsLong(BreakpointContract.BreakpointEntry.COLUMN_ID);

        /* Insert the ContentValues with old podcast ID into database */
        database.insert(
                BreakpointContract.BreakpointEntry.TABLE_NAME,
                null,
                testBreakpointValues);

        /*
         * We don't really care what this ID is, just that it is different than the original and
         * that we can use it to verify our "new" podcast entry has been made.
         */
        long newPodcastId = originalPodcastId + 1;
        Log.v("PodcastDataTest","id... " + newPodcastId);

        testBreakpointValues.put(BreakpointContract.BreakpointEntry.COLUMN_ID, newPodcastId);

        /* Insert the ContentValues with new podcast ID into database */
        database.insert(
                BreakpointContract.BreakpointEntry.TABLE_NAME,
                null,
                testBreakpointValues);

        /* Query for a podcast record with our new podcast ID */
        Cursor queryCursor = database.query(
                BreakpointContract.BreakpointEntry.TABLE_NAME,
                new String[]{BreakpointContract.BreakpointEntry.COLUMN_ID},
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
        database.close();
    }


    private void deleteAllRecordsFromTable() {

        database = dbHelper.getWritableDatabase();
        /* The delete method deletes all of the desired rows from the table, not the table itself */
        database.delete(BreakpointContract.BreakpointEntry.TABLE_NAME, null, null);

        /* Always close the database when you're through with it */
        database.close();
    }
}
