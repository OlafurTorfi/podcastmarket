package com.olafurtorfi.www.podcastmarket.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.PodcastContract;
import com.olafurtorfi.www.podcastmarket.utilities.NetworkUtil;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastSyncTask {
    private static String ifNullMakeEmptyString(String input){
        if(input != null)
            return input;
        return "";
    }
    private static String TAG = "PodcastSynTask";

    synchronized public static void addPodcast(Context context, String urlString) {

        Log.d(TAG, "syncPodcast: try adding podcast");
        try {
            URL podcastRequestUrl = new URL(urlString);
            Log.v(TAG, "syncPodcast: " + urlString);
            String podcastResponse = NetworkUtil.getResponseFromHttpUrl(podcastRequestUrl);

            //you can still change the format of the reader, but it's not recommended.
            SyndFeedInput feedIn = new SyndFeedInput();

            XmlReader xmlReader = new XmlReader(podcastRequestUrl);

            SyndFeed feed = feedIn.build(xmlReader);


            String title = feed.getTitle();
            if (title == null){
                new Toast(context).makeText(context, "unable to read podcast title", Toast.LENGTH_LONG).show();
            } else {
                String author = ifNullMakeEmptyString(feed.getAuthor());
                String description = ifNullMakeEmptyString(feed.getDescription());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference podcasts = database.getReference("podcasts");
                String podcastKey = author+"-"+title;
                podcastKey = podcastKey.replace(".","(dot)").replace("#","(hash)").replace("$","(dollar)").replace("[","(sqrBracketOpen)").replace("]","(sqrBracketClose)");
                DatabaseReference podcast = podcasts.child(podcastKey);
                DatabaseReference pa = podcast.child("author");
                pa.setValue(author);
                DatabaseReference pt = podcast.child("title");
                pt.setValue(title);
                DatabaseReference pu = podcast.child("url");
                pu.setValue(urlString);
                DatabaseReference pd = podcast.child("description");
                pd.setValue(description);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }
    }
    synchronized public static void syncPodcast(Context context, String urlString) {

        Log.d(TAG, "syncPodcast: try syncing podcast");

        try {
            URL podcastRequestUrl = new URL(urlString);
            Log.v(TAG, "syncPodcast: " + urlString);

            /* Use the URL to retrieve the JSON */
            String podcastResponse = NetworkUtil.getResponseFromHttpUrl(podcastRequestUrl);

            /* Parse the JSON into a list of podcast values */
            Log.v(TAG, "syncPodcast: response from url " + podcastResponse);


            //you can still change the format of the reader, but it's not recommended.
            SyndFeedInput feedIn = new SyndFeedInput();

            XmlReader xmlReader = new XmlReader(podcastRequestUrl);

            SyndFeed feed = feedIn.build(xmlReader);

            Log.v(TAG, "syncPodcast: " + feed);

            String author = ifNullMakeEmptyString(feed.getAuthor());

            String title = feed.getTitle();
            if (title == null){
                new Toast(context).makeText(context, "unable to read podcast title", Toast.LENGTH_LONG).show();
            } else{
                String description = ifNullMakeEmptyString(feed.getDescription());
                Log.d(TAG, "syncPodcast: title" + title + ", author: " + author + ", description: " + description);
                ContentValues cv = new ContentValues();
                cv.put("title", title);
                cv.put("description", description);
                cv.put("author", author);
                Uri uri = context.getContentResolver().insert(PodcastContract.PodcastEntry.CONTENT_URI, cv);
//                String podcast = uri.getLastPathSegment();
//                Log.d(TAG, "syncPodcast: last path segment " + podcast);

                List<SyndEntry> entries = feed.getEntries();
                String eTitle = "";
                String eDescription = "";
                String eUri = "";
                String eAuthor = "";
                Date eDate = null;


                for (SyndEntry entry : entries){
                    eTitle = ifNullMakeEmptyString(entry.getTitle());
                    eDescription = ifNullMakeEmptyString(entry.getDescription().getValue());
                    eUri = ifNullMakeEmptyString(entry.getUri());
                    eDate = entry.getPublishedDate();
                    eAuthor =ifNullMakeEmptyString(entry.getAuthor());
                    ContentValues eCv = new ContentValues();
                    eCv.put("title", eTitle);
                    eCv.put("description", eDescription);
                    eCv.put("podcast", title);
                    eCv.put("url", eUri);
                    eCv.put("date", eDate.getTime());
                    eCv.put("author", eAuthor);
                    // this is a hack
//                    ContentValues eCv = new ContentValues();
                    //eCv.put("path", "RohingjarVeraIlluga.mp3");

                    context.getContentResolver().insert(EpisodeContract.EpisodeEntry.CONTENT_URI, eCv);
                    Log.v(TAG, "syncPodcast: eTitle:"+eTitle+", eDescription:"+eDescription+", eUri:"+eUri + ", eDate: "+eDate + ", eAuthor: " + eAuthor+", podcast:"+title);

                }

                Log.v(TAG, "syncPodcast: eTitle:"+title+", eDescription:"+description+", eUri:"+urlString + ", eDate: "+eDate + ", eAuthor: " + author);



                }

        } catch (Exception e) {
            /* Server probably invalid */
            // TODO: show error message to user, this method fails due to intent activity being dead
//            new Toast(context).makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
