package com.appster.adapters.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appster.adapters.ChatGroupDelegateAdapter.ChatGroupClickListener;

/**
 * Created by linh on 09/10/2017.
 */

public class FollowHostSuggestionMessageHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.img_avatar)
    CircleImageView mImgAvatar;
    Context mContext;

    ChatGroupClickListener mListener;

    public FollowHostSuggestionMessageHolder(View itemView, ChatGroupClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        this.mListener = listener;
    }

    public static FollowHostSuggestionMessageHolder create(ViewGroup parent, ChatGroupClickListener listener){
        return new FollowHostSuggestionMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_host_suggestion, parent, false), listener);
    }

    public void bindTo(@NonNull ChatItemModelClass item){
        ImageLoaderUtil.displayUserImage(mContext, item.getProfilePic(), mImgAvatar);
        itemView.setOnClickListener(v -> {
               if (mListener != null) mListener.onFollowHostSuggestionItemClicked(item);
        });
    }
}
