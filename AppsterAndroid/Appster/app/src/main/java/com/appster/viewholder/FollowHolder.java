package com.appster.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.interfaces.FollowHolderListener;
import com.appster.models.FollowItemModel;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CommonDefine;

import butterknife.Bind;

/**
 * Created by User on 10/27/2015.
 */
public class FollowHolder extends BaseViewHolderImageLoader<FollowItemModel, FollowHolderListener> {

    @Bind(R.id.imv_user_image)
    CircleImageView imvUserImage;
    @Bind(R.id.txt_username)
    TextView txtUsername;
    @Bind(R.id.btn_follow)
    Button btnFollow;

    int position;
    private FollowItemModel item;
    private Context context;

    public FollowHolder(View view, FollowItemModel model, Context context, int position) {
        super(view, model, context);
        this.position = position;
        item = model;
        this.context = context;

    }

    @Override
    public void init(Context context, FollowHolderListener listener, FollowItemModel model, int position) {
        super.init(context, listener, model, position);

        this.item = model;
        this.listener = listener;
        this.position = position;

        // Set username
        txtUsername.setText(item.getDisplayName());

        if (AppsterApplication.mAppPreferences.isUserLogin()) {

            if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(item.getUserId())) {

                btnFollow.setVisibility(View.GONE);

            } else {
                btnFollow.setVisibility(View.VISIBLE);
            }

        } else {

            btnFollow.setVisibility(View.GONE);

        }


        // Set follow
        if (item.getIsFollow() == CommonDefine.USER_PROFILE_UN_FOLLOW) {
            btnFollow.setBackgroundResource(R.drawable.btn_follow_newfeed);
            btnFollow.setText(context.getString(R.string.profile_un_follow));
            btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white));

        } else {
            btnFollow.setBackgroundResource(R.drawable.background_btn_live);
            btnFollow.setText(context.getString(R.string.profile_follow));
            btnFollow.setTextColor(ContextCompat.getColor(context, R.color.background_and_text_header));
        }

        ImageLoaderUtil.displayUserImage(context, item.getProfilePic(),
                imvUserImage);

        setHolderListener(listener);

    }

    @Override
    public FollowItemModel getModel() {
        return super.getModel();
    }

    @Override
    protected void setHolderListener(FollowHolderListener listener) {
        super.setHolderListener(listener);

        imvUserImage.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        txtUsername.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.imv_user_image:

                if (listener != null) {
                    listener.viewUserDetail(item);
                }

                break;

            case R.id.btn_follow:
                if (listener != null) {
                    listener.followUser(item, position);
                }
                break;

            case R.id.txt_username:

                if (listener != null) {
                    listener.viewUserDetail(item);
                }

                break;
        }
    }

}
