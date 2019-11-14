package com.appster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.models.HomeCurrentEventModel;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 7/14/2016.
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_HEADER = 1;
    private final int VIEW_ITEM = 2;

    private Context mContext;
    private HomeCurrentEventModel homeCurrentEventModel;

    public EventAdapter(Context mContext, HomeCurrentEventModel homeCurrentEventModel) {
        this.mContext = mContext;
        this.homeCurrentEventModel = homeCurrentEventModel;
    }

    @Override
    public int getItemCount() {

        if (homeCurrentEventModel == null) return 0;
        if (homeCurrentEventModel.getEventDetails() == null) return 0;
        return homeCurrentEventModel.getEventDetails().size();
    }

    @Override
    public int getItemViewType(int position) {

        return position == 0 ? VIEW_HEADER : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = null;
        if (viewType == VIEW_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.event_adapter_header_row, parent, false);
            vh = new EventHeaderViewHolder(v);

        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.event_adapter_row, parent, false);
            vh = new EventHolder(v);

        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventHeaderViewHolder) {

            handleHeader((EventHeaderViewHolder) holder, position);

        } else if (holder instanceof EventHolder) {
            final HomeCurrentEventModel.EventDetailsBean item = homeCurrentEventModel.getEventDetails().get(position);
            handleItem((EventHolder) holder, item, position);
        } else {
            ((BaseRecyclerViewLoadMore.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void handleItem(EventHolder holder, HomeCurrentEventModel.EventDetailsBean item, int position) {
        ImageLoaderUtil.displayMediaImage(mContext, item.getImage(), holder.llBanner);
    }

    private void handleHeader(EventHeaderViewHolder holder, int position) {
        ImageLoaderUtil.displayMediaImage(mContext, homeCurrentEventModel.getImage(), holder.llBanner);
    }

    public class EventHeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ll_banner)
        ImageView llBanner;

        public EventHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EventHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ll_banner)
        ImageView llBanner;

        public EventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
