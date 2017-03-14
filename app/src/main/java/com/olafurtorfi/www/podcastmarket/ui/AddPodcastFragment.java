package com.olafurtorfi.www.podcastmarket.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
import com.olafurtorfi.www.podcastmarket.sync.PodcastSyncIntentService;

public class AddPodcastFragment extends DialogFragment {

    EditText addPodcastInput;

    private static final String TAG = "AddPodcastFragment";

    public AddPodcastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance
     */
    public static AddPodcastFragment newInstance() {
        AddPodcastFragment fragment = new AddPodcastFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_add_podcast, null);
        addPodcastInput = (EditText) rootView.findViewById(R.id.add_podcast_input);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        addPodcastInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addPodcast();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.add_podcast_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPodcast();
                    }
                });

        return builder.create();
    }

    public void addPodcast(){
        Editable text = addPodcastInput.getText();
        Log.d(TAG, "addPodcast: " + text.toString());

        Context context = getContext();
        Intent intent = new Intent(context, PodcastSyncIntentService.class);
        intent.putExtra("urlString", text.toString());
        context.startService(intent);
    }
}
