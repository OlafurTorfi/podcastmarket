package com.olafurtorfi.www.podcastmarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchResultActivity extends ListActivity {

    private static final String TAG = "SearchResultActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();
        String searchText = intent.getStringExtra("searchText");
        String searchType = intent.getStringExtra("searchType");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference podcasts = database.getReference("podcasts");
        Query result = podcasts.orderByChild(searchType).startAt(searchText);
        result.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildren().toString());
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: " + ds.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled, result: " + databaseError.toString() );
            }
        });
    }
}
