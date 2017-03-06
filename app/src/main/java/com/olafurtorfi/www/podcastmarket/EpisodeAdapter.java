package com.olafurtorfi.www.podcastmarket;


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

import com.olafurtorfi.www.podcastmarket.data.EpisodeObject;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeAdapterViewHolder>{

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private EpisodeAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private String TAG = "EpisodeAdapter";

    public EpisodeAdapter(@NonNull Context context, EpisodeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface EpisodeAdapterOnClickHandler {
        void onClick(EpisodeObject episodeId, String tag);
    }

    private Cursor mCursor;

    @Override
    public EpisodeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = R.layout.episode_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new EpisodeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(EpisodeActivity.INDEX_EPISODE_TITLE);
        String description = mCursor.getString(EpisodeActivity.INDEX_EPISODE_DESCRIPTION);
        String filePath = mCursor.getString(EpisodeActivity.INDEX_EPISODE_FILE_PATH);

        Log.v("Episode Adapter", "position "+ position + ", Cursor value " + title + " " + description);
        if(holder != null){
            if(holder.episodeTitleView != null){
                holder.episodeTitleView.setText(title);
            }
            if(holder.episodeDescriptionView != null){
                holder.episodeDescriptionView.setText(description);
            }
            if(filePath == null){
                holder.deleteEpisodeView.setVisibility(View.INVISIBLE);
                holder.playNowView.setVisibility(View.INVISIBLE);
                holder.downloadEpisodeView.setVisibility(View.VISIBLE);
            }
            else{
                holder.deleteEpisodeView.setVisibility(View.VISIBLE);
                holder.playNowView.setVisibility(View.VISIBLE);
                holder.downloadEpisodeView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its podcast data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
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
    class EpisodeAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
//        final ImageView iconView;

        final TextView episodeDescriptionView;

        final TextView episodeTitleView;
        final ImageButton playNowView;
        final ImageButton deleteEpisodeView;
        final ImageButton downloadEpisodeView;

        EpisodeAdapterViewHolder(View view) {
            super(view);
            episodeTitleView = (TextView) view.findViewById(R.id.episode_title);
            episodeDescriptionView = (TextView) view.findViewById(R.id.episode_description);
            playNowView = (ImageButton) view.findViewById(R.id.playNow);
            deleteEpisodeView = (ImageButton) view.findViewById(R.id.deleteEpisode);
            downloadEpisodeView = (ImageButton) view.findViewById(R.id.downloadEpisode);

            playNowView.setOnClickListener(this);

            deleteEpisodeView.setOnClickListener(this);

            downloadEpisodeView.setOnClickListener(this);
//            iconView = (ImageView) view.findViewById(R.id.podcast_icon);

//            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            String title = mCursor.getString(EpisodeActivity.INDEX_EPISODE_TITLE);
            String description = mCursor.getString(EpisodeActivity.INDEX_EPISODE_DESCRIPTION);
            String podcast = mCursor.getString(EpisodeActivity.INDEX_EPISODE_PODCAST);
            String filePath = mCursor.getString(EpisodeActivity.INDEX_EPISODE_FILE_PATH);
            String fileUrl = mCursor.getString(EpisodeActivity.INDEX_EPISODE_FILE_URL);
            String author = mCursor.getString(EpisodeActivity.INDEX_EPISODE_AUTHOR);

            EpisodeObject episodeObject = new EpisodeObject(title, description, podcast, filePath, fileUrl, author);

            mClickHandler.onClick(episodeObject, tag);
        }
    }
}
