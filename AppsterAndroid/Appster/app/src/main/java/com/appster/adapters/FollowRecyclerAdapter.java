package com.appster.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CircleImageView;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.ShowErrorManager;
import com.appster.models.FollowItemModel;
import com.appster.models.FollowUser;
import com.appster.models.UserModel;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 5/5/2016.
 */
public class FollowRecyclerAdapter extends BaseRecyclerViewLoadMore<FollowRecyclerAdapter.FollowHolder, FollowItemModel> {

    private Context context;
    private ArrayList<FollowItemModel> arrFollowers;

    public FollowRecyclerAdapter(Context context, RecyclerView recyclerView, ArrayList<FollowItemModel> mModels) {
        super(recyclerView, mModels);

        this.arrFollowers = mModels;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.follow_user_dapter_row, parent, false);

            vh = new FollowHolder(v);
        } else {
            View v = getProgressBarLayout(parent);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FollowHolder) {

            final FollowItemModel item = mModels.get(position);
            handleItem((FollowHolder) holder, item, position);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public void handleItem(final FollowHolder viewHolder, final FollowItemModel item, final int postiotn) {

        // Set username
        viewHolder.txtUsername.setText(item.getDisplayName());

        if (AppsterApplication.mAppPreferences.isUserLogin()) {

            if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(item.getUserId())) {

                viewHolder.btnFollow.setVisibility(View.GONE);

            } else {
                viewHolder.btnFollow.setVisibility(View.VISIBLE);
            }

        } else {

            viewHolder.btnFollow.setVisibility(View.GONE);

        }

        // Set follow
        if (item.getIsFollow() == Constants.UN_FOLLOW_USER) {
            viewHolder.btnFollow.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_follow));
        } else {
            viewHolder.btnFollow.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_following));
        }

        ImageLoaderUtil.displayUserImage(context, item.getProfilePic(),
                viewHolder.imvUserImage);

        viewHolder.imvUserImage.setOnClickListener(v -> {
            if (isOwnerProfile(AppsterApplication.mAppPreferences.getUserModel(), item.getUserId()))
                return;
            ((BaseToolBarActivity) context).startActivityProfile(item.getUserId(), item.getDisplayName());
        });


        viewHolder.btnFollow.setOnClickListener(v -> followUser(item, postiotn));
        viewHolder.txtUsername.setOnClickListener(v -> viewHolder.imvUserImage.performClick());
    }

    private boolean isOwnerProfile(UserModel userProfile, String userId) {
        return userProfile != null &&
                userProfile.getUserId().equalsIgnoreCase(userId);
    }

    private void followUser(FollowItemModel itemModel, int position) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) context).goingLoginScreen();
        } else {
            if (itemModel.getIsFollow() == Constants.UN_FOLLOW_USER) {

                executeFollowUser(itemModel, position);

            } else {
                DialogUtil.showConfirmUnFollowUser((Activity) context, itemModel.getDisplayName(),
                        () -> executeUnFollowUser(itemModel, position));

            }
        }
    }

    private void executeFollowUser(FollowItemModel itemModel, final int position) {

        DialogManager.getInstance().showDialog(context, context.getResources().getString(R.string.connecting_msg));
        FollowUser followUser = new FollowUser(context, itemModel.getUserId(), true);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                DialogManager.getInstance().dismisDialog();
                arrFollowers.get(position).setIsFollow(Constants.IS_FOLLOWING_USER);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String message) {
                DialogManager.getInstance().dismisDialog();
                if (errorCode == ShowErrorManager.pass_word_required) {
                    handleFollowWithPassword(itemModel, position);
                } else {
                    ((BaseActivity) context).handleError(message, errorCode);
                }
            }
        });
    }

    void followUserWithPassword(String pass, String userId, int position) {
        FollowUser followUser = new FollowUser(context, userId, true);

        followUser.executeFollowWithPass(pass);

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                arrFollowers.get(position).setIsFollow(Constants.IS_FOLLOWING_USER);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String message) {
                ((BaseActivity) context).handleError(message, errorCode);
            }
        });
    }

    private void handleFollowWithPassword(FollowItemModel itemModel, int position) {
        new DialogbeLiveConfirmation.Builder()
                .title(context.getString(R.string.enter_password))
                .setPasswordBox(true)
                .confirmText(context.getString(R.string.verify))
                .onEditTextValue(value -> followUserWithPassword(value, itemModel.getUserId(), position))
                .build().show(context);
    }

    private void executeUnFollowUser(FollowItemModel itemModel, final int position) {

        DialogManager.getInstance().showDialog(context, context.getResources().getString(R.string.connecting_msg));
        FollowUser followUser = new FollowUser((Activity) context, itemModel.getUserId(), false);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                DialogManager.getInstance().dismisDialog();
                arrFollowers.get(position).setIsFollow(Constants.UN_FOLLOW_USER);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String message) {
                DialogManager.getInstance().dismisDialog();
                ((BaseActivity) context).handleError(message, errorCode);
            }
        });
    }

    class FollowHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.imv_user_image)
        CircleImageView imvUserImage;
        @Bind(R.id.txt_username)
        TextView txtUsername;
        @Bind(R.id.btn_follow)
        Button btnFollow;

        public FollowHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
