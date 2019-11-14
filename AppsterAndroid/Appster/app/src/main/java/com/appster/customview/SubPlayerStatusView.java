package com.appster.customview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.appster.R;
import com.appster.features.stream.CountDownAnimation;
import com.appster.features.stream.Role;
import com.appster.features.stream.State;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.glide.BlurTransformation;
import com.apster.common.Utils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 9/6/17.
 */

public class SubPlayerStatusView extends FrameLayout {

    @Bind(R.id.rippleBackground)
    RippleBackground rippleBackground;
    @Bind(R.id.tvUserName)
    CustomFontTextView tvUserName;
    @Bind(R.id.tvCallStatus)
    AnimateTextView tvCallStatus;
    @Bind(R.id.ibEndCall)
    ImageButton ibEndCall;
    @Bind(R.id.civAvatar)
    CircleImageView civAvatar;

    @Bind(R.id.flPlayerStatus)
    FrameLayout llPlayerStatus;

    @Bind(R.id.ivHostAvatar)
    ImageView ivGuestAvatar;
    @Bind(R.id.vGuestAvatarOverlay)
    View mVGuestAvatarOverlay;
    @Bind(R.id.ibProfile)
    ImageButton ibProfile;

    @Bind(R.id.tvCountDown)
    CustomFontTextView tvCountDown;
    @Bind(R.id.flUserInfoContainer)
    FrameLayout flUserInfoContainer;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView mTvDisplayName;
    @Role
    int mRole = Role.VIEWER;
    final int width = Utils.dpToPx(120);
    final int height = Utils.dpToPx(160);

    @State
    int mCurrentState = State.CONNECTING;


    OnClickListener mListener;

    private String mGuestAvatarLink = "";
    private String mGuestDisplayName = "";
    private String mGuesUserName = "";
    private final Handler mHandler = new Handler();
    private final WeakRunnable dismissOptionRunable = new WeakRunnable(this);

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    public SubPlayerStatusView(Context context) {
        super(context);
        init(context);
    }

    public SubPlayerStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //== event listeners ===========================================================================
    @OnClick(R.id.ibEndCall)
    public void onIbEndCallClicked() {
        if (mListener != null) mListener.onEndCallClicked();
    }

//    @OnClick(R.id.flUserInfoContainer)
//    public void onDisplayNameClicked(View view) {
//        AppsterUtility.temporaryLockView(view);
//        if (mListener != null) mListener.onShowProfileClicked(mGuesUserName, mGuestAvatarLink);
//    }

    @OnClick(R.id.ibProfile)
    public void showUserProfile(View view) {
        AppsterUtility.temporaryLockView(view);
        if (mListener != null) mListener.onShowProfileClicked(mGuesUserName, mGuestAvatarLink);
    }

    //== inner methods =============================================================================
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sub_player_state, this, true);
        ButterKnife.bind(this);
        resetState();
    }

    private void loadGuestImage(String avatarUrl) {
        if (avatarUrl.isEmpty()) {
            loadDefaultGuestImage();
        } else {
            ImageLoaderUtil.displayUserImage(getContext(), avatarUrl, ivGuestAvatar, width, height, new BlurTransformation(getContext().getApplicationContext()));
        }
    }

    public void setRole(@Role int role) {
        mRole = role;
        resetState();
    }

    public void updateState(@State int status) {
        Timber.e("updateState %d", status);
        if (mCurrentState == status) {
            Timber.e("updateState same status returned %d", status);
            return;
        }
        if (mCurrentState == State.DISCONNECTED && status == State.DISCONNECTING) {
            Timber.e("mCurrentState is disconnected but received disconnecting");
            return;
        }
        switch (status) {
            case State.ACCEPT:
                onAccept();
                break;
            case State.AWAY:
                onAway();
                break;
            case State.REJECT:
                onReject();
                break;
            case State.NO_ANSWER:
                onNoAnswer();
                break;
            case State.AUDIO_ONLY:
                onAudioOnly();
                break;
            case State.VIDEO_AND_AUDIO:
            case State.CONNECTED:
                onConnected();
                break;
            case State.DISCONNECTING:
                onDisconnecting();
                break;
            case State.CONNECTING:
            case State.DISCONNECTED:
                onConnecting();
                break;
            case State.RECONNECTED:
                onReconnecting();
                break;
            default:
                break;
        }
        postInvalidate();
        mCurrentState = status;
        Timber.e("status ***** %d", status);
    }

    private void onReconnecting() {
        initstate();
        if (ibEndCall.getVisibility() != GONE) ibEndCall.setVisibility(GONE);
        if (mRole == Role.HOST) ibProfile.setVisibility(GONE);
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Reconnecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Reconnecting...");
        mHandler.removeCallbacks(dismissOptionRunable);
        mHandler.postDelayed(dismissOptionRunable, 2000);
    }

    private void onAccept() {
        if (rippleBackground.isRippleAnimationRunning()) rippleBackground.stopRippleAnimation();
        if (ibEndCall.getVisibility() == VISIBLE) ibEndCall.setVisibility(GONE);
        if (tvCallStatus.getVisibility() == VISIBLE) tvCallStatus.setVisibility(GONE);
        if (rippleBackground.getVisibility() == VISIBLE) rippleBackground.setVisibility(GONE);
        if (civAvatar.getVisibility() == VISIBLE) civAvatar.setVisibility(GONE);
        if (tvUserName.getVisibility() == VISIBLE) tvUserName.setVisibility(GONE);
        if (mVGuestAvatarOverlay.getVisibility() == VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        startCountDown();
    }


    private void onAway() {
        commonNotifyState("Away");
        if (ibEndCall.getVisibility() != VISIBLE && mRole == Role.HOST)
            ibEndCall.setVisibility(VISIBLE);
    }

    private void onReject() {
        commonNotifyState("Rejected");
    }

    private void onNoAnswer() {
        commonNotifyState("No Answer");
    }


    private void commonNotifyState(String state) {
        initstate();
        if (rippleBackground.isRippleAnimationRunning()) rippleBackground.stopRippleAnimation();
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        tvCallStatus.setTextAndStopAnimation(state);
        if (ibEndCall.getVisibility() == VISIBLE) ibEndCall.setVisibility(INVISIBLE);
    }


    private void onAudioOnly() {
        rippleBackground.setVisibility(VISIBLE);
        if (tvUserName.getVisibility() != VISIBLE) tvUserName.setVisibility(VISIBLE);
        if (!rippleBackground.isRippleAnimationRunning()) rippleBackground.startRippleAnimation();
        ivGuestAvatar.setVisibility(VISIBLE);
        civAvatar.setVisibility(VISIBLE);
        if (tvCallStatus.getVisibility() == VISIBLE) tvCallStatus.setVisibility(INVISIBLE);
        if (tvCallStatus.getVisibility() != VISIBLE) mVGuestAvatarOverlay.setVisibility(VISIBLE);
    }

    void onConnected() {
        if (mCurrentState == State.DISCONNECTING || mCurrentState == State.DISCONNECTED) {
            Timber.e("disconnecting state but call onConnected");
            return;
        }
        if (rippleBackground.isRippleAnimationRunning()) rippleBackground.stopRippleAnimation();
        ibEndCall.setVisibility(GONE);
        ibProfile.setVisibility(GONE);
        tvCallStatus.setTextAndStopAnimation("Connected");
        tvCallStatus.setVisibility(GONE);
        rippleBackground.setVisibility(GONE);
        civAvatar.setVisibility(GONE);
        ivGuestAvatar.setVisibility(GONE);
        tvCountDown.setVisibility(GONE);
        tvUserName.setVisibility(GONE);
        flUserInfoContainer.setVisibility(VISIBLE);
        mVGuestAvatarOverlay.setVisibility(GONE);
        mCurrentState = State.CONNECTED;
    }

    private void onDisconnecting() {
        initstate();
        if (ibEndCall.getVisibility() != GONE) ibEndCall.setVisibility(GONE);
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Disconnecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Disconnecting...");
    }

    private void onConnecting() {
        if (mCurrentState == State.ACCEPT) return;
        initstate();
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Connecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Connecting...");
        ibEndCall.setVisibility(mRole == Role.HOST ? VISIBLE : INVISIBLE);
    }

    private void initstate() {
        if (!rippleBackground.isRippleAnimationRunning()) rippleBackground.startRippleAnimation();
        if (rippleBackground.getVisibility() != VISIBLE) rippleBackground.setVisibility(VISIBLE);
        if (civAvatar.getVisibility() != VISIBLE) civAvatar.setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
        if (tvCountDown.getVisibility() != GONE) tvCountDown.setVisibility(GONE);
        if (tvUserName.getVisibility() != VISIBLE) tvUserName.setVisibility(VISIBLE);
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        if (flUserInfoContainer.getVisibility() == VISIBLE) flUserInfoContainer.setVisibility(GONE);
        if (ibProfile.getVisibility() != GONE) ibProfile.setVisibility(GONE);
    }

    private void startCountDown() {
        if (tvCountDown.getVisibility() != VISIBLE) tvCountDown.setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
//        mCurrentState = State.CONNECTED;
        new CountDownAnimation(5000, 1000, tvCountDown, () -> {
            if (mListener != null) mListener.onCountDownCompleted();
        }).start();
    }

    public void showScreenOptions() {
        if (mRole == Role.HOST) {
            if (flUserInfoContainer.getVisibility() == VISIBLE)
                flUserInfoContainer.setVisibility(GONE);
            if (ibEndCall.getVisibility() != VISIBLE)
                ibEndCall.setVisibility(VISIBLE);
            if (mCurrentState == State.CONNECTED) {
                ibProfile.setVisibility(VISIBLE);
            } else {
                ibProfile.setVisibility(GONE);
            }
            if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
                mVGuestAvatarOverlay.setVisibility(VISIBLE);
            mHandler.removeCallbacks(dismissOptionRunable);
            mHandler.postDelayed(dismissOptionRunable, 2000);
        } else {
            if (mListener != null) mListener.onShowProfileClicked(mGuesUserName, mGuestAvatarLink);
        }
    }

    public void resetState() {
        mCurrentState = State.CONNECTING;
        rippleBackground.startRippleAnimation();
//        ivGuestAvatar.post(this::loadDefaultGuestImage);
        mGuestAvatarLink = "";
        ibEndCall.setVisibility(mRole == Role.HOST ? VISIBLE : INVISIBLE);
        if (mRole == Role.HOST)
            ibProfile.setVisibility(GONE);
        if (!rippleBackground.isRippleAnimationRunning()) rippleBackground.startRippleAnimation();
        rippleBackground.setVisibility(VISIBLE);
        civAvatar.setVisibility(VISIBLE);
        ivGuestAvatar.setVisibility(VISIBLE);
        mVGuestAvatarOverlay.setVisibility(VISIBLE);
        tvCountDown.setVisibility(INVISIBLE);
        tvUserName.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Connecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Connecting...");
        tvCallStatus.setVisibility(VISIBLE);
        flUserInfoContainer.setVisibility(GONE);
    }

    private void loadDefaultGuestImage() {
        ImageLoaderUtil.displayUserImage(getContext(), R.drawable.user_image_default, ivGuestAvatar, width, height, new BlurTransformation(getContext().getApplicationContext()));
    }

    public void setGuesUserName(String guesUserName) {
        mGuesUserName = guesUserName;
    }

    public void setGuestName(String displayName) {
        mGuestDisplayName = displayName;
        tvUserName.setText(mGuestDisplayName);
        mTvDisplayName.setText(mGuestDisplayName);
    }

    public void setGuestAvatar(String avatarUrl) {
        mGuestAvatarLink = avatarUrl;
        loadGuestImage(avatarUrl);
        ImageLoaderUtil.displayUserImage(getContext(), mGuestAvatarLink, civAvatar);
    }

    private final static class WeakRunnable implements Runnable {
        private final WeakReference<SubPlayerStatusView> mSubPlayerStatusViewWeakReference;

        WeakRunnable(SubPlayerStatusView view) {
            mSubPlayerStatusViewWeakReference = new WeakReference<SubPlayerStatusView>(view);
        }

        @Override
        public void run() {
            final SubPlayerStatusView view = mSubPlayerStatusViewWeakReference.get();
            if (view != null) {
                if (view.mCurrentState != State.AWAY) {
                    view.onConnected();
                }
                view.ibProfile.setVisibility(GONE);
            }
        }
    }

    public interface OnClickListener {
        void onEndCallClicked();

        void onCountDownCompleted();

        void onShowProfileClicked(String userName, String profilePic);
    }
}
