package com.olafurtorfi.www.podcastmarket.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.olafurtorfi.www.podcastmarket.data.PodcastContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by olitorfi on 16/02/2017.
 */

public class OpenPodcastJsonUtil {

    /* Podcast information. Each podcast info is an element of the "list" array */
    private static final String OWM_LIST = "list";

    private static final String OWM_TITLE = "title";
    private static final String OWM_AUTHOR = "author";
    private static final String OWM_DESCRIPTION = "description";

    private static final String OWM_PODCAST = "podcast";

    private static final String OWM_MESSAGE_CODE = "cod";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the podcast over various days from the podcast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullPodcastDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param podcastJsonStr JSON response from server
     *
     * @return Array of Strings describing podcast data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getPodcastContentValuesFromJson(Context context, String podcastJsonStr)
            throws JSONException {

        JSONObject podcastJson = new JSONObject(podcastJsonStr);

        /* Is there an error? */
        if (podcastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = podcastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray jsonPodcastArray = podcastJson.getJSONArray(OWM_LIST);


        ContentValues[] podcastContentValues = new ContentValues[jsonPodcastArray.length()];

        /*
         * OWM returns daily podcasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our podcast.
         */
        for (int i = 0; i < jsonPodcastArray.length(); i++) {

            String title;
            String author;
            String description;

            int podcastId;

            /* Get the JSON object representing the day */
            JSONObject podcast = jsonPodcastArray.getJSONObject(i);

            title = podcast.getString(OWM_TITLE);
            author = podcast.getString(OWM_AUTHOR);
            description = podcast.getString(OWM_DESCRIPTION);


            /*
             * Description is in a child array called "podcast", which is 1 element long.
             * That element also contains a podcast code.
             */
            JSONObject podcastObject =
                    podcast.getJSONArray(OWM_PODCAST).getJSONObject(0);


            ContentValues podcastValues = new ContentValues();
            podcastValues.put(PodcastContract.PodcastEntry.COLUMN_TITLE, title);
            podcastValues.put(PodcastContract.PodcastEntry.COLUMN_AUTHOR, author);
            podcastValues.put(PodcastContract.PodcastEntry.COLUMN_DESCRIPTION, description);

            podcastContentValues[i] = podcastValues;
        }

        return podcastContentValues;
    }
}
