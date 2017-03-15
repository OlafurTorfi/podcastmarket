package com.olafurtorfi.www.podcastmarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.olafurtorfi.www.podcastmarket.data.PodcastObject;

public class SearchResultActivity extends ListActivity {

    private static final String TAG = "SearchResultActivity";
    private BasicPodcastAdapter mAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_result);
        
        Intent intent = getIntent();
        String searchText = intent.getStringExtra("searchText");
        String searchType = intent.getStringExtra("searchType");
        
        //query firebase 
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference podcasts = database.getReference("podcasts");
        Query result = podcasts.orderByChild(searchType).startAt(searchText);
        result.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildren().toString());
                PodcastObject[] list = new PodcastObject[(int) dataSnapshot.getChildrenCount()];
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PodcastObject po = new PodcastObject(
                            (String) ds.child("title").getValue(),
                            (String) ds.child("author").getValue(),
                            (String) ds.child("description").getValue(),
                            (String) ds.child("url").getValue());
                    list[count] = po;
                    count++;
                    Log.d(TAG, "onDataChange: " + ds.toString());
                    Log.d(TAG, "onDataChange podcast: " + po.getTitle());
                }
                mAdapter.setList(list);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);
                if (count > 0) showDataView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled, result: " + databaseError.toString() );
            }
        });

        mAdapter = new BasicPodcastAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mAdapter);
    }
}
