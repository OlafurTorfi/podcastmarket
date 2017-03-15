package com.olafurtorfi.www.podcastmarket.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.olafurtorfi.www.podcastmarket.R;
import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.PodcastContract;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.PodcastAdapterViewHolder>{

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private PodcastAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public PodcastAdapter(@NonNull Context context, PodcastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface PodcastAdapterOnClickHandler {
        void onClick(String podcast);
    }

    /*
     * Flag to determine if we want to use a separate view for the list item that represents
     * today. This flag will be true when the phone is in portrait mode and false when the phone
     * is in landscape. This flag will be set in the constructor of the adapter by accessing
     * boolean resources.
     */
    private boolean mUseTodayLayout;

    private Cursor mCursor;

    @Override
    public PodcastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = R.layout.podcast_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new PodcastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PodcastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(PodcastsActivity.INDEX_PODCAST_TITLE);
        String author = mCursor.getString(PodcastsActivity.INDEX_PODCAST_AUTHOR);

        Log.d("Podcast Adapter", "position "+ position + ", Cursor value " + title);
        holder.podcastTitleView.setText(title);
        holder.podcastAuthorView.setText(author);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its podcast data. This method is called by
     * PodcastsActivity after a load has finished, as well as when the Loader responsible for loading
     * the podcast data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a podcast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class PodcastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        final ImageView iconView;

        final TextView podcastAuthorView;

        final TextView podcastTitleView;
        final ImageButton unsubscribeButton;

        PodcastAdapterViewHolder(View view) {
            super(view);
            podcastTitleView = (TextView) view.findViewById(R.id.podcast_title);
            podcastAuthorView = (TextView) view.findViewById(R.id.podcast_author);
            unsubscribeButton = (ImageButton) view.findViewById(R.id.unsubscribe_button);
//            iconView = (ImageView) view.findViewById(R.id.podcast_icon);
            unsubscribeButton.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {

            String tag = (String) v.getTag();
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String podcast = mCursor.getString(PodcastsActivity.INDEX_PODCAST_TITLE);
            Log.v("Podcast Adapter", v.toString() + " clicked, with tag " + tag + ", podcast title is: " + podcast);
            switch (tag){
                case "unsubscribeButton":
                    long id = mCursor.getLong(PodcastsActivity.INDEX_PODCAST_ID);
                    mContext.getContentResolver().delete(PodcastContract.PodcastEntry.CONTENT_URI, PodcastContract.PodcastEntry.COLUMN_ID + " = ?",new String[]{String.valueOf(id)});
                    mContext.getContentResolver().delete(EpisodeContract.EpisodeEntry.CONTENT_URI, EpisodeContract.EpisodeEntry.COLUMN_PODCAST + " = ?",new String[]{podcast});
                    new Toast(mContext).makeText(mContext,podcast + " removed from subscriptions and episodes deleted",Toast.LENGTH_LONG).show();
                    break;
                case "podcastListItem":
                    mClickHandler.onClick(podcast);
                    break;
            }
            mCursor.close();

        }
    }
}
