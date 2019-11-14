package com.appster.features.home.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.models.HomeItemModel;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 5/25/17.
 */

public class HomeItemViewHolder extends RecyclerView.ViewHolder {


    public interface OnClickListener {
        void onHomeItemUserImageClicked(HomeItemModel model, int position);

        void onHomeItemUserNameClicked(HomeItemModel model, int position);
    }

    @Bind(R.id.flBlur)
    FrameLayout flBlur;
    @Bind(R.id.userImage)
    ImageView userImage;
    @Bind(R.id.tvCurrentView)
    TextView tvCurrentView;
    @Bind(R.id.tvStreamType)
    CustomFontTextView streamStype;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView userName;
    @Bind(R.id.tvStreamTitle)
    TextView txtNameStream;
    @Bind(R.id.tvCategory)
    TextView tagCatagory;
    HomeItemModel mHomeItemModel;

    public HomeItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static HomeItemViewHolder create(@NonNull ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.home_adapter_stream_row, parent, false);
        return new HomeItemViewHolder(itemView);
    }

    public void bindTo(HomeItemModel item, @NonNull OnClickListener listener, boolean shouldShowCategoryTag, boolean shouldUseDistanceTag) {
        this.mHomeItemModel = item;
        if (item.isIsRecorded() && item.getStatus() == Constants.StreamStatus.StreamEnd) {
            streamStype.setText(itemView.getContext().getString(R.string.streaming_recorded));
            streamStype.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.home_btn_recorded));
        } else {
            streamStype.setText(itemView.getContext().getString(R.string.streaming_live));
            streamStype.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.home_btn_live));
        }

//        followCount.setText(String.valueOf(item.getFollowerCount()));
        tvCurrentView.setText(String.valueOf(item.getViewCount()));
        userName.setText(StringUtil.decodeString(item.getPublisher().getDisplayName()));
        txtNameStream.setText(StringUtil.decodeString(item.getTitle()));

        String coverThumbnailImage = "";
//        if (item.getPublisher().getUserImage() != null && !item.getPublisher().getUserImage().isEmpty()) {
//            coverThumbnailImage = item.getPublisher().getUserImage();
//        } else {
//            coverThumbnailImage = item.getCoverImage();
//        }

        if (!StringUtil.isNullOrEmptyString(item.getCoverImage())) {
            coverThumbnailImage = item.getCoverImage();
        } else if (item.getPublisher().getUserImage() != null && !item.getPublisher().getUserImage().isEmpty()) {
            coverThumbnailImage = item.getPublisher().getUserImage();
        }

        ImageLoaderUtil.displayUserImage(itemView.getContext(), coverThumbnailImage, userImage);
        itemView.setOnClickListener(view -> listener.onHomeItemUserImageClicked(mHomeItemModel, getAdapterPosition()));
        userName.setOnClickListener(view -> listener.onHomeItemUserNameClicked(mHomeItemModel, getAdapterPosition()));
        if (shouldShowCategoryTag) {
            tagCatagory.setText(item.getTagName());
            //temp solution for xmas theme
        } else if (shouldUseDistanceTag) {
            tagCatagory.setText(String.valueOf(item.getDistance()));
        } else {
            tagCatagory.setVisibility(View.INVISIBLE);
        }
    }

}
