package com.appster.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.customview.CircleImageView;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.models.FollowUser;
import com.appster.models.InviteFriendUserModel;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 12/16/2015.
 */
public class AdapterInviteFriend extends RecyclerView.Adapter<AdapterInviteFriend.InviteFriendHolder> {

    Context context;
    private ArrayList<InviteFriendUserModel> arrUser;

    private InviteFriendHolder currentHolder;

    public AdapterInviteFriend(Context context, ArrayList<InviteFriendUserModel> arrUser) {
        this.context = context;
        this.arrUser = arrUser;
    }

    @Override
    public int getItemCount() {

        if (arrUser != null) {
            return arrUser.size();
        }

        return 0;
    }

    @Override
    public void onBindViewHolder(final InviteFriendHolder holder, final int position) {

        final InviteFriendUserModel item = arrUser.get(position);

        if (!StringUtil.isNullOrEmptyString(item.getDisplayName())) {
            holder.txtUsername.setText(item.getDisplayName());
        }

        // Set user image
        ImageLoaderUtil.displayUserImage(context, item.getProfilePic(),
                holder.imvUserImage);

        if (item.getIs_follow() == Constants.IS_FOLLOWING_USER) {
            holder.btnFollow.setBackgroundResource(R.drawable.following_short);
        } else {
            holder.btnFollow.setBackgroundResource(R.drawable.follow_short);
        }

        holder.btnFollow.setOnClickListener(v -> {

            currentHolder = holder;
            holder.btnFollow.setClickable(false);

            if (item.getIs_follow() == CommonDefine.USER_PROFILE_UN_FOLLOW) {
                executeFollowUser(item, position);
            } else if (item.getIs_follow() == CommonDefine.USER_PROFILE_IS_FOLLOW) {
                DialogUtil.showConfirmUnFollowUser((Activity) context, item.getDisplayName(),
                        () -> executeUnFollowUser(item, position), () -> holder.btnFollow.setClickable(true));
            }
        });

        holder.imvUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) context).startActivityProfile(item.getUserId(), item.getDisplayName());
            }
        });

        holder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) context).startActivityProfile(item.getUserId(), item.getDisplayName());
            }
        });

    }

    @Override
    public InviteFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.invite_friend_row_recyclerview, parent, false);
        InviteFriendHolder vh = new InviteFriendHolder(v);
        return vh;
    }

    class InviteFriendHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.imv_user_image)
        CircleImageView imvUserImage;
        @Bind(R.id.txt_username)
        TextView txtUsername;
        @Bind(R.id.btn_follow)
        Button btnFollow;

        public InviteFriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void executeFollowUser(InviteFriendUserModel itemModel, final int position) {

        FollowUser followUser = new FollowUser((Activity) context, itemModel.getUserId(), true);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {

                if (currentHolder != null) {
                    currentHolder.btnFollow.setClickable(true);
                }

                arrUser.get(position).setIs_follow(CommonDefine.USER_PROFILE_IS_FOLLOW);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String message) {

                if (currentHolder != null) {
                    currentHolder.btnFollow.setClickable(true);
                }

                ((BaseActivity) context).handleError(message,
                        errorCode);
            }
        });
    }

    void executeUnFollowUser(InviteFriendUserModel itemModel, final int position) {


        FollowUser followUser = new FollowUser((Activity) context, itemModel.getUserId(), false);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {

                if (currentHolder != null) {
                    currentHolder.btnFollow.setClickable(true);
                }

                arrUser.get(position).setIs_follow(CommonDefine.USER_PROFILE_UN_FOLLOW);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String message) {
                if (currentHolder != null) {
                    currentHolder.btnFollow.setClickable(true);
                }

                ((BaseActivity) context).handleError(message,
                        errorCode);
            }
        });

    }
}
