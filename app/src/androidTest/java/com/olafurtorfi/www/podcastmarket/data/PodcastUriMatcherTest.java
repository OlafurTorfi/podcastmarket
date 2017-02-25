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
public class PodcastUriMatcherTest {

    private static String TAG = "PodcastUriMatcherTest";

    private static final Uri TEST_PODCAST_DIR = PodcastContract.PodcastEntry.CONTENT_URI;
    private static final Uri TEST_PODCAST_WITH_TITLE_DIR = PodcastContract.PodcastEntry
            .buildPodcastUriWithTitle("Testpodcast");
    private static final Uri TEST_PODCAST_OF_ID_DIR = PodcastContract.PodcastEntry.buildPodcastUriWithId(123);

    private static final String podcastCodeVariableName = "CODE_PODCAST";
    private static int REFLECTED_PODCAST_CODE;

    private static final String podcastCodeWithTitleVariableName = "CODE_PODCAST_WITH_TITLE";
    private static int REFLECTED_PODCAST_WITH_TITLE_CODE;

    private static final String podcastCodeOfIdVariableName = "CODE_PODCAST_OF_ID";
    private static int REFLECTED_PODCAST_OF_ID_CODE;

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

            REFLECTED_PODCAST_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    podcastCodeVariableName);

            REFLECTED_PODCAST_WITH_TITLE_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    podcastCodeWithTitleVariableName);

            REFLECTED_PODCAST_OF_ID_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    podcastCodeOfIdVariableName);

            Log.d(TAG, "before: " + podcastCodeVariableName + REFLECTED_PODCAST_CODE+podcastCodeWithTitleVariableName +REFLECTED_PODCAST_WITH_TITLE_CODE+podcastCodeOfIdVariableName+REFLECTED_PODCAST_OF_ID_CODE);

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

        /* Test that the code returned from our matcher matches the expected podcast code */
        String podcastUriDoesNotMatch = "Error: The CODE_PODCAST URI was matched incorrectly.";
        int actualPodcastCode = testMatcher.match(TEST_PODCAST_DIR);
        int expectedPodcastCode = REFLECTED_PODCAST_CODE;
        assertEquals(podcastUriDoesNotMatch,
                expectedPodcastCode,
                actualPodcastCode);

        /*
         * Test that the code returned from our matcher matches the expected podcast with title code
         */
        String podcastWithTitleUriCodeDoesNotMatch =
                "Error: The CODE_PODCAST WITH TITLE URI was matched incorrectly.";
        int actualPodcastWithTitleCode = testMatcher.match(TEST_PODCAST_WITH_TITLE_DIR);
        int expectedPodcastWithTitleCode = REFLECTED_PODCAST_WITH_TITLE_CODE;
        Log.d(TAG, "testUriMatcher: actual" + actualPodcastWithTitleCode+ ", Expected:" + expectedPodcastWithTitleCode);
        assertEquals(podcastWithTitleUriCodeDoesNotMatch,
                expectedPodcastWithTitleCode,
                actualPodcastWithTitleCode);
        /*
         * Test that the code returned from our matcher matches the expected podcast with id code
         */
        String podcastOfDateUriCodeDoesNotMatch =
                "Error: The CODE_PODCAST WITH TITLE URI was matched incorrectly.";
        int actualPodcastOfIdCode = testMatcher.match(TEST_PODCAST_OF_ID_DIR);
        int expectedPodcastOfIdCode = REFLECTED_PODCAST_OF_ID_CODE;
        assertEquals(podcastOfDateUriCodeDoesNotMatch,
                expectedPodcastOfIdCode,
                actualPodcastOfIdCode);
    }
}