package com.olafurtorfi.www.podcastmarket.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.olafurtorfi.www.podcastmarket.R;
import com.olafurtorfi.www.podcastmarket.data.PodcastObject;

/**
 * Created by olitorfi on 14/03/2017.
 */

public class BasicPodcastAdapter extends RecyclerView.Adapter<BasicPodcastAdapter.BasicPodcastAdapterViewHolder>{


    private final Context mContext;
    private PodcastObject[] list;


    public BasicPodcastAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setList(PodcastObject[] list){
        this.list = list;
    }

    @Override
    public BasicPodcastAdapter.BasicPodcastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.podcast_list_item;

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

    public class BasicPodcastAdapterViewHolder  extends RecyclerView.ViewHolder {
        final TextView podcastAuthorView;

        final TextView podcastTitleView;

        public BasicPodcastAdapterViewHolder(View view) {
            super(view);
            podcastTitleView = (TextView) view.findViewById(R.id.podcast_title);
            podcastAuthorView = (TextView) view.findViewById(R.id.podcast_author);
        }
    }
}
