package com.appster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontButton;
import com.appster.features.login.LoginActivity;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.ShowErrorManager;
import com.appster.models.FollowUser;
import com.appster.models.TopFanModel;
import com.appster.models.UserModel;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;
import com.apster.common.DialogbeLiveConfirmation;

import java.util.ArrayList;

/**
 * Created by User on 8/31/2016.
 */
public class TopPanAdapter extends BaseRecyclerViewLoadMore<TopPanAdapter.Holder, TopFanModel> {
    Context context;
    Holder currentHolder;
    ArrayList<TopFanModel> mModels;
    private boolean isViewLiveStream;
    private DialogUserProfileFragment.UserProfileActionListener mUserProfileActionListener;
    private boolean isViewer;

    public TopPanAdapter(Context context, RecyclerView recyclerView, ArrayList<TopFanModel> mModels, boolean isViewLiveStream, boolean isViewer) {
        super(recyclerView, mModels);
        this.context = context;
        this.mModels = mModels;
        this.isViewLiveStream = isViewLiveStream;
        this.isViewer = isViewer;
    }

    @Override
    public void handleItem(final Holder viewHolder, final TopFanModel item, final int postiotn) {
        if (postiotn == 0 || postiotn == 1 || postiotn == 2) {
            viewHolder.topImage.setVisibility(View.VISIBLE);
            viewHolder.numberOrder.setVisibility(View.GONE);
            viewHolder.borderStt.setVisibility(View.VISIBLE);

            if (postiotn == 0) {
                viewHolder.topImage.setBackgroundResource(R.drawable.top_fans_one);
                viewHolder.borderStt.setBackgroundResource(R.drawable.topfan_silver_1th);
            } else if (postiotn == 1) {
                viewHolder.topImage.setBackgroundResource(R.drawable.top_fans_two);
                viewHolder.borderStt.setBackgroundResource(R.drawable.topfan_silver_2th);
            } else if (postiotn == 2) {
                viewHolder.topImage.setBackgroundResource(R.drawable.top_fans_three);
                viewHolder.borderStt.setBackgroundResource(R.drawable.topfan_silver_3th);
            }

        } else {
            viewHolder.topImage.setVisibility(View.GONE);
            viewHolder.numberOrder.setVisibility(View.VISIBLE);
            viewHolder.numberOrder.setText(postiotn + 1 + "");
            viewHolder.borderStt.setBackgroundResource(R.drawable.topfan_silver_normal);
        }

        ImageLoaderUtil.displayUserImage(context, item.getUserImage(), viewHolder.userImage);
        viewHolder.displayName.setText(item.getDisplayName());
        viewHolder.totalStars.setText(String.valueOf(item.getTotalGoldSend()));

        if (item.getIsFollow() == Constants.IS_FOLLOWING_USER) {
            viewHolder.btnFollow.setBackgroundResource(R.drawable.btn_following);
        } else {
            viewHolder.btnFollow.setBackgroundResource(R.drawable.btn_follow);
        }

        viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!AppsterApplication.mAppPreferences.isUserLogin()) {

                    try {
                        ((BaseToolBarActivity) context).goingLoginScreen();
                    } catch (ClassCastException e) {
                        startActivityLogin();
                    }
                    return;
                }

                currentHolder = viewHolder;
                viewHolder.btnFollow.setClickable(false);

                boolean isFollowing = item.getIsFollow() == 1;
                if (isFollowing) {
                    DialogUtil.showConfirmUnFollowUser((Activity) context, item.getDisplayName(),
                            () -> executeFollowUser(item, postiotn, false), () -> viewHolder.btnFollow.setClickable(true));
                } else {
                    executeFollowUser(item, postiotn, true);
                }

            }
        });

        if (AppsterApplication.mAppPreferences.isUserLogin() &&
                AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(item.getUserId() + "")) {
            viewHolder.btnFollow.setVisibility(View.GONE);
        } else {
            viewHolder.btnFollow.setVisibility(View.VISIBLE);
        }

        viewHolder.userImage.setOnClickListener(view -> {
            if (isOwnerProfile(AppsterApplication.mAppPreferences.getUserModel(), item.getUserId()))
                return;
            if (isViewLiveStream) {
                showUserProfile(item.getUserName(), item.getUserImage());
            } else {
                ((BaseActivity) context).startActivityProfile(item.getUserId(), item.getDisplayName());
            }
        });

//        viewHolder.displayName.setOnClickListener(view -> {
//            viewHolder.userImage.performClick();
//        });
        viewHolder.llUserInfo.setOnClickListener(view -> viewHolder.userImage.performClick());

    }

    private boolean isOwnerProfile(UserModel userProfile, String userId) {
        return userProfile != null &&
                userProfile.getUserId().equalsIgnoreCase(userId);
    }

    public void setUserProfileActionListener(DialogUserProfileFragment.UserProfileActionListener userProfileActionListener) {
        mUserProfileActionListener = userProfileActionListener;
    }

    void showUserProfile(String userName, String userImage) {
        DialogUserProfileFragment userProfileFragment = DialogUserProfileFragment.newInstance(userName, userImage, isViewer, true);
        userProfileFragment.setUserProfileActionListener(mUserProfileActionListener);
        userProfileFragment.show(((BaseActivity) context).getSupportFragmentManager(), "UserProfileView");

    }

    public void startActivityLogin() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_in_up, R.anim.keep_view_animation);
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent, options.toBundle());

    }

    void executeFollowUser(TopFanModel itemModel, final int position, boolean isFollow) {

        FollowUser followUser = new FollowUser(context, itemModel.getUserId(), isFollow);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                mModels.get(position).setIsFollow(isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
                notifyDataSetChanged();
                currentHolder.btnFollow.setClickable(true);
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == ShowErrorManager.pass_word_required) {
                    handleFollowWithPassword(itemModel, position);
                } else {
                    ((BaseActivity) context).handleError(message, errorCode);
                }

                currentHolder.btnFollow.setClickable(true);
            }
        });
    }

    void followUserWithPassword(String pass, String userId, int position) {
        FollowUser followUser = new FollowUser(context, userId, true);

        followUser.executeFollowWithPass(pass);

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                mModels.get(position).setIsFollow(isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
                notifyDataSetChanged();
                currentHolder.btnFollow.setClickable(true);
            }

            @Override
            public void onError(int errorCode, String message) {
                ((BaseActivity) context).handleError(message, errorCode);
                currentHolder.btnFollow.setClickable(true);
            }
        });
    }

    private void handleFollowWithPassword(TopFanModel itemModel, int position) {
        new DialogbeLiveConfirmation.Builder()
                .title(context.getString(R.string.enter_password))
                .setPasswordBox(true)
                .confirmText(context.getString(R.string.verify))
                .onEditTextValue(value -> followUserWithPassword(value, itemModel.getUserId(), position))
                .build().show(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(context).inflate(
                    R.layout.top_pan_dapter_row, parent, false);

            vh = new Holder(v);
        } else {
            View v = getProgressBarLayout(parent);

            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Holder) {

            final TopFanModel item = mModels.get(position);
            handleItem((Holder) holder, item, position);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView topImage;
        TextView numberOrder;
        CircleImageView userImage;
        TextView displayName;
        LinearLayout llUserInfo;
        TextView userName;
        CustomFontButton btnFollow;
        TextView totalStars;
        LinearLayout borderStt;

        public Holder(View itemView) {
            super(itemView);

            topImage = (ImageView) itemView.findViewById(R.id.top_image);
            numberOrder = (TextView) itemView.findViewById(R.id.numberOrder);
            userImage = (CircleImageView) itemView.findViewById(R.id.userImage);
            displayName = (TextView) itemView.findViewById(R.id.displayName);
            llUserInfo = (LinearLayout) itemView.findViewById(R.id.ll_user_info);
            userName = (TextView) itemView.findViewById(R.id.userName);
            btnFollow = (CustomFontButton) itemView.findViewById(R.id.btn_follow);
            totalStars = (TextView) itemView.findViewById(R.id.totalStars);
            borderStt = (LinearLayout) itemView.findViewById(R.id.border_stt);
        }
    }
}
