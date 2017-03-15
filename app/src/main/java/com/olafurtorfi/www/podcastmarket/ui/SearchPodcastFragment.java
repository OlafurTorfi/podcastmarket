package com.olafurtorfi.www.podcastmarket.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.olafurtorfi.www.podcastmarket.R;

public class SearchPodcastFragment extends DialogFragment {

    EditText searchPodcastInput;
    int itemSelected = 0;
    private static final String TAG = "SearchPodcastFragment";

    public SearchPodcastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance
     * @return A new instance of fragment SearchPodcastFragment.
     */
    public static SearchPodcastFragment newInstance() {
        SearchPodcastFragment fragment = new SearchPodcastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_search_podcast, null);
        searchPodcastInput = (EditText) rootView.findViewById(R.id.search_podcast_input);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        searchPodcastInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    searchPodcast();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        String[] choices = {"title","author"};
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.search_podcast_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        searchPodcast();
                    }
                })
                .setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemSelected(which);
                    }
                })
                .setTitle(R.string.search_podcast_title);

        return builder.create();
    }

    public void itemSelected(int which){
        itemSelected = which;
    }

    public void searchPodcast(){
        Editable text = searchPodcastInput.getText();
        String searchType = "title";
        switch(itemSelected){
            case 0:
                searchType = "title";
                break;
            case 1:
                searchType = "author";
                break;
            default:
                Log.e(TAG, "searchPodcast choice error, defaulting to titles by: " + text.toString());
                break;
        }
        Log.d(TAG, "searchPodcast by " + searchType + " with :" + text.toString());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(getContext(), SearchResultActivity.class);
            intent.putExtra("searchText", text.toString());
            intent.putExtra("searchType", searchType);
            startActivity(intent);
        }
        else{
            Log.e(TAG, "searchPodcast: not compatable with android version");
        }
    }
}
