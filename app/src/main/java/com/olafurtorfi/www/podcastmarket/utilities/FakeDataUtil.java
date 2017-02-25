package com.olafurtorfi.www.podcastmarket.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.PodcastContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class FakeDataUtil {

    private static String TAG = "FakeDataUtil";

    public static ContentValues createTestPodcastValues(String testString) {
        ContentValues testPodcastValues = new ContentValues();
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_TITLE, testString);
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_AUTHOR, testString + "_maker");
        testPodcastValues.put(PodcastContract.PodcastEntry.COLUMN_DESCRIPTION, testString + " is about imporant stuff" );
        return testPodcastValues;
    }
    public static ContentValues createTestEpisodeValues(String title, long podcastId, String whatsitabout) {
        ContentValues testPodcastValues = new ContentValues();
        testPodcastValues.put(EpisodeContract.EpisodeEntry.COLUMN_TITLE, title);
        testPodcastValues.put(EpisodeContract.EpisodeEntry.COLUMN_PODCAST, podcastId);
        testPodcastValues.put(EpisodeContract.EpisodeEntry.COLUMN_DESCRIPTION, title + " is an episode about " + whatsitabout );
        testPodcastValues.put(EpisodeContract.EpisodeEntry.COLUMN_DATE, System.currentTimeMillis());
        testPodcastValues.put(EpisodeContract.EpisodeEntry.COLUMN_FILE_URL, "http://traffic.libsyn.com/dancarlinhh/dchha48_Prophets_of_Doom.mp3");
        return testPodcastValues;
    }
    private static void addABunchOfEpisodesToPodcast(String podcastTitle, String about, long podcastId, List<ContentValues> fakeValues){
        String itsabout;
        for (int i = 0; i <= 2; i++){
            itsabout = "";
            for(int n = 0; n <= i; n++){
                itsabout += about;
            }
            itsabout += "important stuff";
            fakeValues.add(FakeDataUtil.createTestEpisodeValues(podcastTitle + " Episode " + i, podcastId, itsabout));
        }
    }
    public static ContentValues[] makeFakeEpisodes(){
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        addABunchOfEpisodesToPodcast("Fjórða kastið", "lots of ", 3,fakeValues);
        addABunchOfEpisodesToPodcast("Hittkast", "much ", 1, fakeValues);
        addABunchOfEpisodesToPodcast("Sjötta kastið", "seriously ", 5, fakeValues);
        addABunchOfEpisodesToPodcast("Torfakast", "very ", 0, fakeValues);
        addABunchOfEpisodesToPodcast("Ónefnda kastid", "this and ", 4,fakeValues);
        addABunchOfEpisodesToPodcast("ÞriðjaKast", "such ", 2, fakeValues);
        return fakeValues.toArray(new ContentValues[fakeValues.size()]);
    }
    public static ContentValues[] makeFakePodcasts(){
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();

        fakeValues.add(FakeDataUtil.createTestPodcastValues("Fjórða kastið, með langa nafnið"));
        fakeValues.add(FakeDataUtil.createTestPodcastValues("HittKast"));
        fakeValues.add(FakeDataUtil.createTestPodcastValues("Sjötta"));
        fakeValues.add(FakeDataUtil.createTestPodcastValues("Torfakast"));
        fakeValues.add(FakeDataUtil.createTestPodcastValues("ÞriðjaKast"));

        return fakeValues.toArray(new ContentValues[fakeValues.size()]);
    }

    public static void insertFakePodcasts(Context context) {
        // Bulk Insert our new podcast data into the Database
        context.getContentResolver().bulkInsert(
                PodcastContract.PodcastEntry.CONTENT_URI,
                makeFakePodcasts());
    }

    public static void insertFakeEpisodes(Context context) {
        Log.i("FakeDataUtil", "insert some fake episodes");

        // Bulk Insert our new episode data into the Database
        context.getContentResolver().bulkInsert(
                EpisodeContract.EpisodeEntry.CONTENT_URI,
                makeFakeEpisodes());
    }

    public static String getFakeJson() {
        return "{\n" +
                "  \"podcast\": [\n" +
                "    {\n" +
                "      \"title\":\"testpodcast\",\n" +
                "      \"author\":\"tester\",\n" +
                "      \"description\":\"this is test\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\":\"another\",\n" +
                "      \"author\":\"the other\",\n" +
                "      \"description\":\"there is another jedi\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
