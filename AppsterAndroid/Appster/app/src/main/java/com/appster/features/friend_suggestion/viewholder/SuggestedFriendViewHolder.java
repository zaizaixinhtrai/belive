package com.appster.features.friend_suggestion.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.models.SearchModel;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.apster.common.Constants.IS_FOLLOWING_USER;

/**
 * Created by linh on 18/05/2017.
 */

public class SuggestedFriendViewHolder extends RecyclerView.ViewHolder {


    @Bind(R.id.userImage)
    ImageView userImage;
    @Bind(R.id.tvHashtag)
    CustomFontTextView tvHashtag;
    @Bind(R.id.tvUserName)
    CustomFontTextView tvUserName;
    @Bind(R.id.btnFollow)
    Button btnFollow;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView tvDisplayName;
    SearchModel searchModel;

    public interface OnClickListener {
        void onSuggestedItemClicked(SearchModel item, int position);

        void onFollowUserClicked(SearchModel item, int position);
    }

    public SuggestedFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static SuggestedFriendViewHolder create(ViewGroup parent) {
        return new SuggestedFriendViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggested_friend_list, parent, false));
    }


    public void bindTo(SearchModel model, SuggestedFriendViewHolder.OnClickListener itemCallBack) {
        this.searchModel = model;
        userImage.setTag(this);
        itemView.setOnClickListener(view -> {
            if (itemCallBack != null)
                itemCallBack.onSuggestedItemClicked(getSearchModel(), getAdapterPosition());
        });
        btnFollow.setOnClickListener(view -> {
            if (itemCallBack != null)
                itemCallBack.onFollowUserClicked(getSearchModel(), getAdapterPosition());
        });
        String imageUrl = searchModel.getUserImage();
        tvUserName.setText(String.format("@%s", searchModel.getUserName()));
        tvDisplayName.setText(String.valueOf(searchModel.getDisplayName()));
        btnFollow.setBackground(ContextCompat.getDrawable(itemView.getContext(), searchModel.getIsFollow() == IS_FOLLOWING_USER ? R.drawable.suggested_following : R.drawable.suggested_follow));
        // display other user image
        ImageLoaderUtil.displayUserImage(userImage.getContext(), imageUrl, userImage);
    }

    private SearchModel getSearchModel() {
        return searchModel;
    }
}
