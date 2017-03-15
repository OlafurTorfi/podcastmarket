package com.olafurtorfi.www.podcastmarket.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.olafurtorfi.www.podcastmarket.R;
import com.olafurtorfi.www.podcastmarket.data.PodcastObject;
import com.olafurtorfi.www.podcastmarket.sync.PodcastSyncIntentService;

/**
 * Created by olitorfi on 14/03/2017.
 */

public class BasicPodcastAdapter extends RecyclerView.Adapter<BasicPodcastAdapter.BasicPodcastAdapterViewHolder>{


    private final Context mContext;
    private PodcastObject[] list;

    private static final String TAG = "BasicPodcastAdapter";

    public BasicPodcastAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setList(PodcastObject[] list){
        this.list = list;
    }

    @Override
    public BasicPodcastAdapter.BasicPodcastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.search_result_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new BasicPodcastAdapter.BasicPodcastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BasicPodcastAdapter.BasicPodcastAdapterViewHolder holder, int position) {
        if (position < this.list.length){
            holder.podcastAuthorView.setText(this.list[position].getAuthor());
            holder.podcastTitleView.setText(this.list[position].getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if(this.list != null){
            return this.list.length;
        }
        return 0;
    }

    public class BasicPodcastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView podcastAuthorView;

        final TextView podcastTitleView;

        final ImageButton subscribeImageButton;

        public BasicPodcastAdapterViewHolder(View view) {
            super(view);
            podcastTitleView = (TextView) view.findViewById(R.id.podcast_title);
            podcastAuthorView = (TextView) view.findViewById(R.id.podcast_author);
            subscribeImageButton = (ImageButton) view.findViewById(R.id.subscribeButton);
            subscribeImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if(list != null){
                Log.d(TAG, "onClick position: " + adapterPosition);
                if(list.length > adapterPosition){
                    Intent intent = new Intent(mContext, PodcastSyncIntentService.class);
                    intent.setAction("subscribe");
                    intent.putExtra("urlString", list[adapterPosition].getUrl());
                    mContext.startService(intent);
                    new Toast(mContext).makeText(mContext, list[adapterPosition].getTitle() + " added to subscriptions",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
