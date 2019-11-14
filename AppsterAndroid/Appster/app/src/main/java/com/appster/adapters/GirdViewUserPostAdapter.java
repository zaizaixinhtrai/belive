package com.appster.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.models.UserModel;
import com.appster.models.UserPostModel;
import com.appster.utility.ImageLoaderUtil;
import com.appster.viewholder.OnItemClickListener;
import com.apster.common.Constants;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.List;


/**
 * Created by SonNguyen on 22/04/2016.
 */
public class GirdViewUserPostAdapter extends BaseRecyclerViewLoadMore<GirdViewUserPostAdapter.NewsFeedGridViewHolder, UserPostModel> {

    private List<UserPostModel> arrayListNewFeeds;
    final Context context;
    private OnItemClickListener onItemClickListener;
    UserModel userProfileDetails;

    public GirdViewUserPostAdapter(RecyclerView recyclerView, Context mContext, List<UserPostModel> arrayListNewFeeds) {
        super(recyclerView, arrayListNewFeeds);
        this.context = mContext;
        setItemsAndNotify(arrayListNewFeeds);
    }

    public void setUserProfileDetails(UserModel userProfileDetails) {
        this.userProfileDetails = userProfileDetails;
    }

    public List<UserPostModel> getItems() {
        return arrayListNewFeeds;
    }

    public void setItemsAndNotify(List<UserPostModel> arrayListNewFeeds) {
        this.arrayListNewFeeds = arrayListNewFeeds;// filteredData(arrayListNewFeeds);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        if (viewType == VIEW_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_grid_newsfeed, parent, false);

            vh = new NewsFeedGridViewHolder(v, onItemClickListener);
        } else {
            v = getProgressBarLayout(parent);
            vh = new ProgressViewHolder(v);

        }
        v.setTag(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsFeedGridViewHolder) {
            ((NewsFeedGridViewHolder) holder).bindView(arrayListNewFeeds.get(position), position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        if (arrayListNewFeeds != null) return arrayListNewFeeds.size();
        return 0;
    }

    @Override
    public void handleItem(NewsFeedGridViewHolder viewHolder, UserPostModel item, int postiotn) {

    }

    public class NewsFeedGridViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView image_media;
        TextView streamStype;
        LinearLayout infoStream;
        TextView tagCatagory;
        TextView durationTime;

        private OnItemClickListener onItemClickListener;

        public NewsFeedGridViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            image_media = (ImageView) itemView.findViewById(R.id.mediaImage);
            streamStype = (TextView) itemView.findViewById(R.id.tvStreamType);
            infoStream = (LinearLayout) itemView.findViewById(R.id.info_stream);
            tagCatagory = (TextView) itemView.findViewById(R.id.tvCategory);
            durationTime = (TextView) itemView.findViewById(R.id.duration_time);
        }

        public void bindView(final UserPostModel itemFeed, final int position) {

            if (itemFeed.getType() == Constants.LIST_USER_POST_NOMAL && itemFeed.getPost() != null) {

                String urlImageLoad = itemFeed.getPost().getMediaImage();
                if (itemFeed.getPost().getMediaType() == 1 && !StringUtil.isNullOrEmptyString(itemFeed.getPost().getThumbnailImage())) {
                    urlImageLoad = itemFeed.getPost().getThumbnailImage();
                }
                ImageLoaderUtil.displayMediaImage(context, urlImageLoad, image_media);

                image_media.setVisibility(View.VISIBLE);
                if (itemFeed.getPost().getMediaType() == Constants.VIDEOS_FEED) {
                    streamStype.setVisibility(View.VISIBLE);
                    streamStype.setText(context.getString(R.string.newsfeed_btn_video));
                } else {
                    streamStype.setVisibility(View.GONE);
                }

                infoStream.setVisibility(View.GONE);
            } else if (itemFeed.getType() == Constants.LIST_USER_POST_LIVE_STREAM && itemFeed.getStream() != null) {
                image_media.setVisibility(View.VISIBLE);
                String thumbUrl = itemFeed.getStream().getCoverImage();
                if (TextUtils.isEmpty(thumbUrl)){
                    thumbUrl = itemFeed.getStream().getPublisher().getUserImage();
                }
                ImageLoaderUtil.displayUserImage(context.getApplicationContext(), thumbUrl, image_media);
                streamStype.setVisibility(View.VISIBLE);
                if (itemFeed.getStream().isIsRecorded()) {
                    streamStype.setText(context.getString(R.string.streaming_recorded));
                } else {
                    streamStype.setText(context.getString(R.string.streaming_live));
                }
                infoStream.setVisibility(View.VISIBLE);
                durationTime.setText(SetDateTime.convertSecondsToTime(itemFeed.getStream().getDuration()));
                tagCatagory.setText(itemFeed.getStream().getTagName());
            }

            itemView.setTag(itemFeed);
            image_media.setTag(position);
            itemView.setOnClickListener(this);
            image_media.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, itemView.getTag(), (int) image_media.getTag());
            }
        }
    }
}
