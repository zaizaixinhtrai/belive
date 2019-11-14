package com.appster.customview;

import android.view.View;

import com.appster.R;
import com.appster.features.stream.Role;
import com.appster.features.stream.State;
import com.appster.utility.AppsterUtility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 9/7/17.
 */

public class SubPlayerLayout implements SubPlayerStatusView.OnClickListener {


    @Bind(R.id.subPlayerStatus)
    SubPlayerStatusView subPlayerStatus;

    View root;

    @Role
    int mRole = Role.VIEWER;

    boolean mTapScreenEnable = false;


    OnClickListener mClickListener;


    public void setClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public SubPlayerLayout(View view) {
        ButterKnife.bind(this, view);
        root = view;
        subPlayerStatus.setListener(this);

    }


    public void setRole(int role) {
        mRole = role;
        subPlayerStatus.setRole(role);
    }

    public void setGuestAvatar(String avatarUrl) {
        if (subPlayerStatus != null) subPlayerStatus.setGuestAvatar(avatarUrl);
    }

    public void setGuestDisplayName(String displayName) {
        if (subPlayerStatus != null) subPlayerStatus.setGuestName(displayName);
    }

    public void setGuestUserName(String userName) {
        if (subPlayerStatus != null) subPlayerStatus.setGuesUserName(userName);
    }

    public void updateState(@State int status) {
        switch (status) {
            case State.DISCONNECTING:
            case State.DISCONNECTED:
                mTapScreenEnable = false;
                break;
            default:
                break;
        }
        subPlayerStatus.updateState(status);
    }


    @OnClick(R.id.subPlayerStatus)
    public void onPlayerClicked() {
        AppsterUtility.temporaryLockView(subPlayerStatus);
        if (mTapScreenEnable) {
            subPlayerStatus.showScreenOptions();
        }
    }

    public View getView() {
        return root;
    }

    public boolean isTapScreenEnable() {
        return mTapScreenEnable;
    }

    public void streamStarted() {
        updateState(State.CONNECTED);
        mTapScreenEnable = true;
        Timber.e("streamStarted");
//        subPlayerStatus.setVisibility(GONE);
    }


    boolean mIsReload = false;
    long currentRecordedPos = 0;

    @Override
    public void onEndCallClicked() {
        if (mClickListener != null) mClickListener.onEndCallClicked();
    }

    @Override
    public void onCountDownCompleted() {
        if (mClickListener != null) mClickListener.onCountDownCompleted();
    }

    @Override
    public void onShowProfileClicked(String userName, String profilePic) {
        if (mClickListener != null) mClickListener.onShowProfileClicked(userName, profilePic);
    }

    public interface OnClickListener {
        void onEndCallClicked();
        void onCountDownCompleted();
        void onShowProfileClicked(String userName, String profilePic);
    }
}
