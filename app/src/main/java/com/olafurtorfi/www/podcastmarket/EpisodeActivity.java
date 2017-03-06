package com.olafurtorfi.www.podcastmarket;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.EpisodeObject;
import com.olafurtorfi.www.podcastmarket.utilities.NetworkUtil;

public class EpisodeActivity  extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EpisodeAdapter.EpisodeAdapterOnClickHandler{

    public static final String[] MAIN_EPISODE_PROJECTION = {
            EpisodeContract.EpisodeEntry.COLUMN_ID,
            EpisodeContract.EpisodeEntry.COLUMN_TITLE,
            EpisodeContract.EpisodeEntry.COLUMN_AUTHOR,
            EpisodeContract.EpisodeEntry.COLUMN_PODCAST,
            EpisodeContract.EpisodeEntry.COLUMN_DESCRIPTION,
            EpisodeContract.EpisodeEntry.COLUMN_FILE_PATH,
            EpisodeContract.EpisodeEntry.COLUMN_FILE_URL
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_EPISODE_ID = 0;
    public static final int INDEX_EPISODE_TITLE = 1;
    public static final int INDEX_EPISODE_AUTHOR = 2;
    public static final int INDEX_EPISODE_PODCAST = 3;
    public static final int INDEX_EPISODE_DESCRIPTION = 4;
    public static final int INDEX_EPISODE_FILE_PATH = 5;
    public static final int INDEX_EPISODE_FILE_URL = 6;



    /*
     * This ID will be used to identify the Loader responsible for loading our podcast podcast. In
     * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     * Please note that 44 was chosen arbitrarily. You can use whatever number you like, so long as
     * it is unique and consistent.
     */
    private static final int ID_EPISODE_LOADER = 45;

    private EpisodeAdapter mEpisodeAdapter;

    private RecyclerView mRecyclerView;

    private int mPosition = android.support.v7.widget.RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;
    private String TAG = "Episode Activity";
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        getSupportActionBar().setElevation(0f);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_episodes);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_episodes_loading_indicator);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mEpisodeAdapter = new EpisodeAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mEpisodeAdapter);

        showLoading();

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for EpisodeActivity cannot be null");
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_EPISODE_LOADER, null, this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.list, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.i("MainActivity", "creating loader, get cursor");
        switch (loaderId) {

            case ID_EPISODE_LOADER:
                /* URI for all rows of podcast data in our podcast table */
//                Uri episodeQueryUri = EpisodeContract.EpisodeEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = EpisodeContract.EpisodeEntry.COLUMN_TITLE + " ASC";

                Log.i("EpisodeActivity", mUri.toString());

                return new CursorLoader(this,
                        mUri,
                        MAIN_EPISODE_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("MainActivity", "log finished, cursor count: " + data.getCount());

        mEpisodeAdapter.swapCursor(data);
        if (mPosition == android.support.v7.widget.RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showEpisodeDataView();
    }

    private void showEpisodeDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the podcast data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mEpisodeAdapter.swapCursor(null);
    }

    @Override
    public void onClick(EpisodeObject episode, String tag) {
        Log.d("Episode Activity", "On click.....");
        if(tag.equals(getString(R.string.download_episode_button_tag))){
            Log.d(TAG, "onClick: " + NetworkUtil.isDownloadManagerAvailable(this));
            if(NetworkUtil.isDownloadManagerAvailable(this)) {
                Uri uri = Uri.parse(episode.fileUrl);
                Log.d(TAG, "onClick: " + episode.fileUrl);
                DownloadManager dl = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDescription("Downloading podcast episode " + episode.title);
                request.setAllowedOverMetered(false);
                request.setTitle("Download Podcast");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(this,null,"testfile");
                long result = dl.enqueue(request);
                Log.d(TAG, "onClick: " + result);
//                Uri uriForDownloadedFile = dl.getUriForDownloadedFile(result);
//                Log.d(TAG, "onClick: " + uriForDownloadedFile.toString());

                ConnectivityManager cm =
                        (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                Log.d(TAG, "onClick: wifi?" + cm.TYPE_WIFI);
                Log.d(TAG, "onClick: is connected ?" + isConnected);

                Cursor dlCursor = dl.query(new DownloadManager.Query());
                Log.d(TAG, "onClick: " + dlCursor.getCount());
                new Toast(this).makeText(this, "download of " + episode.title + " started", Toast.LENGTH_SHORT).show();
            } else {
                new Toast(this).makeText(this, "download manager unavailable for android versions older than Gingerbread", Toast.LENGTH_LONG).show();
            }
        } else if(tag.equals(getString(R.string.add_to_playlist_button_tag))){

        } else if(tag.equals(getString(R.string.play_button_tag))){
            Log.i("Episode Adapter", "play now clicked......");

            Intent playerIntent = new Intent(EpisodeActivity.this, PlayerActivity.class);
            playerIntent.putExtra("title",episode.title);
            playerIntent.putExtra("description",episode.description);
            playerIntent.putExtra("podcast",episode.podcast);
            playerIntent.putExtra("author",episode.author);
            playerIntent.putExtra("filePath",episode.filePath);
            playerIntent.putExtra("fileUrl",episode.fileUrl);
            startActivity(playerIntent);
        }
    }


    private void showLoading() {
        /* Then, hide the podcast data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
