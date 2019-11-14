package com.appster.features.friend_suggestion.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.core.expanableadapter.ChildViewHolder;
import com.appster.customview.CircleImageView;
import com.appster.domain.FriendSuggestionModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linh on 18/05/2017.
 */

public class FriendOnBeliveViewHolder extends ChildViewHolder {
    @Bind(R.id.ivUserImage)
    CircleImageView userImage;
    @Bind(R.id.tvDisplayName)
    TextView displayName;
    @Bind(R.id.tvUserName)
    TextView userName;
    @Bind(R.id.btnFollow)
    ImageButton btnFollow;

    private FriendSuggestionModel item;

    public static FriendOnBeliveViewHolder create(ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_suggestion, parent, false);
        return new FriendOnBeliveViewHolder(itemView);
    }

    FriendOnBeliveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(FriendSuggestionModel usersItemModel, FriendOnBeLiveViewHolderListener listener) {
        item = usersItemModel;
        ImageLoaderUtil.displayUserImage(itemView.getContext(), item.getUserImage(), userImage);
        displayName.setText(item.getDisplayName());
        userName.setText(String.format("@%s", item.getUserName()));

        if (AppsterApplication.mAppPreferences.getUserModel() != null && AppsterApplication.mAppPreferences.getUserModel().getUserId().equalsIgnoreCase(usersItemModel.getUserId())) {
            btnFollow.setVisibility(View.INVISIBLE);
        }else if (usersItemModel.getIsFollow() == Constants.UN_FOLLOW_USER){
            btnFollow.setImageResource(R.drawable.suggested_follow);
        }else{
            btnFollow.setImageResource(R.drawable.suggested_following);
        }
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClicked(v, getModel(), getAdapterPosition());
            }
        });
        userImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAvatarImgClicked(v, getModel(), getAdapterPosition());
            }
        });
        btnFollow.setOnClickListener(v -> {
            if (listener != null) {
                AppsterUtility.temporaryLockView(v);
                listener.onFollowButtonClicked(v, getModel(), getAdapterPosition());
            }
        });
    }

    FriendSuggestionModel getModel() {
        return item;
    }

    public interface FriendOnBeLiveViewHolderListener {
        void onFollowButtonClicked(View v, FriendSuggestionModel userItemModel, int position);

        void onAvatarImgClicked(View v, FriendSuggestionModel userItemModel, int position);
        void onItemClicked(View v, FriendSuggestionModel userItemModel, int position);
    }
}
