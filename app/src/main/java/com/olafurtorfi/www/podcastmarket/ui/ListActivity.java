package com.olafurtorfi.www.podcastmarket.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.olafurtorfi.www.podcastmarket.R;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";
    
    public RecyclerView mRecyclerView;
    public ProgressBar mLoadingIndicator;
    public TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_list);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_list_loading_indicator);

        mErrorMessage = (TextView) findViewById(R.id.error_message);



        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        getSupportActionBar().setElevation(0f);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        showLoading();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, PodcastsActivity.class));
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_add_podcast) {

            Log.d(TAG, "onOptionsItemSelected: add podcast");
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            AddPodcastFragment newDialog = AddPodcastFragment.newInstance();
            newDialog.show(ft,"Add Podcast");
            return true;
        } else if (id == R.id.action_search_podcasts) {
            Log.d(TAG, "onOptionsItemSelected: search podcasts");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            SearchPodcastFragment newDialog = SearchPodcastFragment.newInstance();
            newDialog.show(ft,"Search Podcast");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the podcast data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
    }

    private void showLoading() {
        /* Then, hide the podcast data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
    }


    public void showError(String message){
//        mErrorMessage = (TextView) findViewById(R.id.error_message);
        mErrorMessage.setText(message);
        mErrorMessage.setVisibility(View.VISIBLE);
//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_list);
        mRecyclerView.setVisibility(View.GONE);
//        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.GONE);
    }
}
