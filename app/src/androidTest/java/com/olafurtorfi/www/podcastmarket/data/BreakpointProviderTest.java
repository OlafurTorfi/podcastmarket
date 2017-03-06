package com.olafurtorfi.www.podcastmarket.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.olafurtorfi.www.podcastmarket.utilities.FakeDataUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by olitorfi on 23/02/2017.
 */
@RunWith(AndroidJUnit4.class)
public class BreakpointProviderTest {
//    BreakpointProvider breakpointProvider;
    private String TAG = "BreakpointProviderTest";

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    @Before
    public void setUp() {
        deleteAllRecordsFromTable();
    }
    @After
    public void deleteRecords(){
        deleteAllRecordsFromTable();
    }

    @Test
    public void testInsert() {
        /* Create a new array of ContentValues for breakpoint */
        ContentValues cv = FakeDataUtil.makeFakeBreakpoints()[0];

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (breakpoint) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                observer);

        /* bulkInsert will return the number of records that were inserted. */
        Uri uri = contentResolver.insert(
                /* URI at which to insert data */
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Array of values to insert into given URI */
                cv);

        observer.waitForNotificationOrFail();

        contentResolver.unregisterContentObserver(observer);

        Cursor cursor = mContext.getContentResolver().query(
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort by date from smaller to larger (past to future) */
                BreakpointContract.BreakpointEntry.COLUMN_TIME + " ASC");

        assertEquals("Cursor should have exactly one row after insert",cursor.getCount(), 1);

        cursor.moveToFirst();
        TestUtilities.validateCurrentRecord(
                "testBulkInsert. Error validating BreakpointEntry ",
                cursor,
                cv);

        cursor.close();
    }

    @Test
    public void testBasicBreakpointQuery() {

        /* Use PodcastDbHelper to get access to a writable database */
        PodcastDbHelper dbHelper = new PodcastDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Obtain breakpoint values from TestUtilities */
        ContentValues testBreakpointValues = FakeDataUtil.makeFakeBreakpoints()[0];

        /* Insert ContentValues into database and get a row ID back */
        long breakpointRowId = database.insert(
                /* Table to insert values into */
                BreakpointContract.BreakpointEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testBreakpointValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, breakpointRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /*
         * Perform our ContentProvider query. We expect the cursor that is returned will contain
         * the exact same data that is in testBreakpointValues and we will validate that in the next
         * step.
         */
        Cursor breakpointCursor = mContext.getContentResolver().query(
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        /* This method will ensure that we  */
        TestUtilities.validateThenCloseCursor("testBasicBreakpointQuery",
                breakpointCursor,
                testBreakpointValues);
    }

    @Test
    public void testBulkInsert() {
        int BULK_INSERT_RECORDS_TO_INSERT = 2;
        /* Create a new array of ContentValues for breakpoint */
        ContentValues[] bulkInsertTestContentValues = FakeDataUtil.makeFakeBreakpoints();

        /*
         * TestContentObserver allows us to test breakpoint or not notifyChange was called
         * appropriately. We will use that here to make sure that notifyChange is called when a
         * deletion occurs.
         */
        TestUtilities.TestContentObserver breakpointObserver = TestUtilities.getTestContentObserver();

        /*
         * A ContentResolver provides us access to the content model. We can use it to perform
         * deletions and queries at our CONTENT_URI
         */
        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (breakpoint) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                breakpointObserver);

        /* bulkInsert will return the number of records that were inserted. */
        int insertCount = contentResolver.bulkInsert(
                /* URI at which to insert data */
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Array of values to insert into given URI */
                bulkInsertTestContentValues);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        breakpointObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(breakpointObserver);

        /*
         * We expect that the number of test content values that we specify in our TestUtility
         * class were inserted here. We compare that value to the value that the ContentProvider
         * reported that it inserted. These numbers should match.
         */
        String expectedAndActualInsertedRecordCountDoNotMatch =
                "Number of expected records inserted does not match actual inserted record count";
        assertEquals(expectedAndActualInsertedRecordCountDoNotMatch,
                insertCount,
                BULK_INSERT_RECORDS_TO_INSERT);

        /*
         * Perform our ContentProvider query. We expect the cursor that is returned will contain
         * the exact same data that is in testBreakpointValues and we will validate that in the next
         * step.
         */
        Cursor cursor = mContext.getContentResolver().query(
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort by date from smaller to larger (past to future) */
                BreakpointContract.BreakpointEntry.COLUMN_TIME + " ASC");

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);



        /*
         * We now loop through and validate each record in the Cursor with the expected values from
         * bulkInsertTestContentValues.
         */
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord(
                    "testBulkInsert. Error validating BreakpointEntry " + i,
                    cursor,
                    bulkInsertTestContentValues[i]);
        }

        /* Always close the Cursor! */
        cursor.close();
    }

    @Test
    public void testDeleteAllRecordsFromProvider() {

        testBulkInsert();
        TestUtilities.TestContentObserver breakpointObserver = TestUtilities.getTestContentObserver();

        /*
         * A ContentResolver provides us access to the content model. We can use it to perform
         * deletions and queries at our CONTENT_URI
         */
        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (breakpoint) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                breakpointObserver);

        /* Delete all of the rows of data from the breakpoint table */
        contentResolver.delete(
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null);

        /* Perform a query of the data that we've just deleted. This should be empty. */
        Cursor shouldBeEmptyCursor = contentResolver.query(
                BreakpointContract.BreakpointEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        breakpointObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(breakpointObserver);

        /* In some cases, the cursor can be null. That's actually a failure case here. */
        String cursorWasNull = "Cursor was null.";
        assertNotNull(cursorWasNull, shouldBeEmptyCursor);

        /* If the count of the cursor is not zero, all records weren't deleted */
        String allRecordsWereNotDeleted =
                "Error: All records were not deleted from breakpoint table during delete";
        assertEquals(allRecordsWereNotDeleted,
                0,
                shouldBeEmptyCursor.getCount());

        /* Always close your cursor */
        shouldBeEmptyCursor.close();
    }

    private void deleteAllRecordsFromTable() {
        /* Access writable database through PodcastDbHelper */
        PodcastDbHelper helper = new PodcastDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* The delete method deletes all of the desired rows from the table, not the table itself */
        database.delete(BreakpointContract.BreakpointEntry.TABLE_NAME, null, null);

        /* Always close the database when you're through with it */
        database.close();
    }
}
