package com.olafurtorfi.www.podcastmarket.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by olitorfi on 20/02/2017.
 */
@RunWith(AndroidJUnit4.class)
public class BreakpointUriMatcherTest {

    private static String TAG = "BreakpUriMatcherTest";

    private static final Uri TEST_BREAKPOINT_DIR = BreakpointContract.BreakpointEntry.CONTENT_URI;
    private static final Uri TEST_BREAKPOINT_WITH_PODCAST_AND_EPISODE_DIR = BreakpointContract.BreakpointEntry
            .buildBreakpointUriWithPodcastAndEpisodeTitles("Testkast","breakpoint1");

    private static final String breakpointCodeVariableName = "CODE_BREAKPOINT";
    private static int REFLECTED_BREAKPOINT_CODE;

    private static final String breakpointCodeWithPodcastAndEpisodeVariableName = "CODE_BREAKPOINT_WITH_PODCAST_AND_EPISODE";
    private static int REFLECTED_BREAKPOINT_WITH_PODCAST_AND_EPISODE_CODE;

    private UriMatcher testMatcher;


    private static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        Integer value = (Integer) intField.get(null);
        return value;
    }


    @Before
    public void before() {
        try {

            Method buildUriMatcher = PodcastProvider.class.getDeclaredMethod("buildUriMatcher");
            testMatcher = (UriMatcher) buildUriMatcher.invoke(PodcastProvider.class);

            REFLECTED_BREAKPOINT_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    breakpointCodeVariableName);

            REFLECTED_BREAKPOINT_WITH_PODCAST_AND_EPISODE_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    breakpointCodeWithPodcastAndEpisodeVariableName);

        } catch (NoSuchFieldException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            String noBuildUriMatcherMethodFound =
                    "It doesn't appear that you have created a method called buildUriMatcher in " +
                            "the PodcastProvider class.";
            fail(noBuildUriMatcherMethodFound);
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Students: This function tests that your UriMatcher returns the correct integer value for
     * each of the Uri types that our ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        /* Test that the code returned from our matcher matches the expected breakpoint code */
        String breakpointUriDoesNotMatch = "Error: The CODE_BREAKPOINT URI was matched incorrectly.";
        int actualBreakpointCode = testMatcher.match(TEST_BREAKPOINT_DIR);
        int expectedBreakpointCode = REFLECTED_BREAKPOINT_CODE;
        assertEquals(breakpointUriDoesNotMatch,
                expectedBreakpointCode,
                actualBreakpointCode);

        /*
         * Test that the code returned from our matcher matches the expected breakpoint with podcast and episode code
         */
        String breakpointOfPodcastUriCodeDoesNotMatch =
                "Error: The CODE_BREAKPOINT WITH TITLE URI was matched incorrectly.";
        int actualBreakpointOfPodcastCode = testMatcher.match(TEST_BREAKPOINT_WITH_PODCAST_AND_EPISODE_DIR);
        int expectedBreakpointOfPodcastCode = REFLECTED_BREAKPOINT_WITH_PODCAST_AND_EPISODE_CODE;
        Log.d(TAG, "testUriMatcher: actual" + actualBreakpointOfPodcastCode+ ", Expected:" + expectedBreakpointOfPodcastCode);
        assertEquals(breakpointOfPodcastUriCodeDoesNotMatch,
                expectedBreakpointOfPodcastCode,
                actualBreakpointOfPodcastCode);
    }
}