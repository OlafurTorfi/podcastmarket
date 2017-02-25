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
public class EpisodeUriMatcherTest {

    private static String TAG = "EpisodeUriMatcherTest";

    private static final Uri TEST_EPISODE_DIR = EpisodeContract.EpisodeEntry.CONTENT_URI;
    private static final Uri TEST_EPISODE_OF_PODCAST_DIR = EpisodeContract.EpisodeEntry
            .buildEpisodeUriWithPodcast(321);
    private static final Uri TEST_EPISODE_OF_ID_DIR = EpisodeContract.EpisodeEntry.buildEpisodeUriWithId(123);

    private static final String episodeCodeVariableName = "CODE_EPISODE";
    private static int REFLECTED_EPISODE_CODE;

    private static final String episodeCodeOfPodcastVariableName = "CODE_EPISODE_OF_PODCAST";
    private static int REFLECTED_EPISODE_WITH_TITLE_CODE;

    private static final String episodeCodeOfIdVariableName = "CODE_EPISODE_OF_ID";
    private static int REFLECTED_EPISODE_OF_ID_CODE;

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

            REFLECTED_EPISODE_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    episodeCodeVariableName);

            REFLECTED_EPISODE_WITH_TITLE_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    episodeCodeOfPodcastVariableName);

            REFLECTED_EPISODE_OF_ID_CODE = getStaticIntegerField(
                    PodcastProvider.class,
                    episodeCodeOfIdVariableName);

            Log.d(TAG, "before: " + episodeCodeVariableName + REFLECTED_EPISODE_CODE+episodeCodeOfPodcastVariableName +REFLECTED_EPISODE_WITH_TITLE_CODE+episodeCodeOfIdVariableName+REFLECTED_EPISODE_OF_ID_CODE);

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

        /* Test that the code returned from our matcher matches the expected episode code */
        String episodeUriDoesNotMatch = "Error: The CODE_EPISODE URI was matched incorrectly.";
        int actualEpisodeCode = testMatcher.match(TEST_EPISODE_DIR);
        int expectedEpisodeCode = REFLECTED_EPISODE_CODE;
        assertEquals(episodeUriDoesNotMatch,
                expectedEpisodeCode,
                actualEpisodeCode);

        /*
         * Test that the code returned from our matcher matches the expected episode with title code
         */
        String episodeOfPodcastUriCodeDoesNotMatch =
                "Error: The CODE_EPISODE WITH TITLE URI was matched incorrectly.";
        int actualEpisodeOfPodcastCode = testMatcher.match(TEST_EPISODE_OF_PODCAST_DIR);
        int expectedEpisodeOfPodcastCode = REFLECTED_EPISODE_WITH_TITLE_CODE;
        Log.d(TAG, "testUriMatcher: actual" + actualEpisodeOfPodcastCode+ ", Expected:" + expectedEpisodeOfPodcastCode);
        assertEquals(episodeOfPodcastUriCodeDoesNotMatch,
                expectedEpisodeOfPodcastCode,
                actualEpisodeOfPodcastCode);
        /*
         * Test that the code returned from our matcher matches the expected episode with id code
         */
        String episodeOfDateUriCodeDoesNotMatch =
                "Error: The CODE_EPISODE WITH TITLE URI was matched incorrectly.";
        int actualEpisodeOfIdCode = testMatcher.match(TEST_EPISODE_OF_ID_DIR);
        int expectedEpisodeOfIdCode = REFLECTED_EPISODE_OF_ID_CODE;
        assertEquals(episodeOfDateUriCodeDoesNotMatch,
                expectedEpisodeOfIdCode,
                actualEpisodeOfIdCode);
    }
}