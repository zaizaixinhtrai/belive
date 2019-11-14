package com.appster.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.appster.R;
import com.appster.features.stream.CountDownAnimation;
import com.appster.features.stream.State;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;
import com.pack.utility.BitmapUtil;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.appster.R.id.vGuestAvatarOverlay;

/**
 * Created by thanhbc on 9/7/17.
 */

public class SubStreamStatusView extends FrameLayout {

    @Bind(R.id.ivHostAvatar)
    ImageView ivGuestAvatar;

    @Bind(R.id.ibEndCall)
    ImageButton ibEndCall;
    @Bind(R.id.ivSwitchCam)
    ImageView ivSwitchCam;
//    @Bind(R.id.ivVideoControl)
//    ImageView ivVideoControl;

    @Bind(R.id.tvCountDown)
    CustomFontTextView tvCountDown;

    @Bind(R.id.flUserInfoContainer)
    FrameLayout flUserInfoContainer;

    @Bind(R.id.llCameraActionContainer)
    LinearLayout llCameraActionContainer;
    @Bind(R.id.civAvatar)
    CircleImageView civAvatar;
    @Bind(R.id.rippleBackground)
    RippleBackground rippleBackground;
    @Bind(R.id.tvUserName)
    CustomFontTextView tvUserName;
    @Bind(R.id.tvCallStatus)
    AnimateTextView tvCallStatus;

    @Bind(R.id.tvDisplayName)
    CustomFontTextView tvDisplayName;

    private boolean mEnableCamera = true;
    @Bind(vGuestAvatarOverlay)
    View mVGuestAvatarOverlay;
    OnClickListener mListener;

    final int width = Utils.dpToPx(120);
    final int height = Utils.dpToPx(160);
    private String mGuestAvatarLink = "";
    private String mGuestDisplayName = "";
    LinearLayout.LayoutParams paramsAudioOnly;
    LinearLayout.LayoutParams paramsNormal;
    private final Handler mHandler = new Handler();
    private final WeakRunnable dismissOptionRunable = new WeakRunnable(this);
    @State
    private int mCurrentState = State.CONNECTING;

    public SubStreamStatusView(Context context) {
        super(context);
        init(context);
    }

    public SubStreamStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sub_camera_state, this, true);
        ButterKnife.bind(this);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ivGuestAvatar.post(this::loadGuestImage);

    }

    public void setGuestName(String displayName) {
        mGuestDisplayName = displayName;
        tvUserName.setText(mGuestDisplayName);
        tvDisplayName.setText(mGuestDisplayName);
    }

    public void setGuestAvatar(String avatarUrl) {
        mGuestAvatarLink = avatarUrl;
        loadGuestImage();
        ImageLoaderUtil.displayUserImage(getContext(), mGuestAvatarLink, civAvatar);
    }

    private void loadGuestImage() {
        if (mGuestAvatarLink.isEmpty()) {
            loadDefaultGuestImage();
        } else {
            ImageLoaderUtil.displayUserImage(getContext(), mGuestAvatarLink, ivGuestAvatar, true, width, height, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    ivGuestAvatar.setImageResource(R.drawable.user_image_default);
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (ivGuestAvatar != null)
                        ivGuestAvatar.setImageBitmap(BitmapUtil.blurImage(bitmap, 1f, 50));
                }
            });
        }
    }

    private void loadDefaultGuestImage() {

        ImageLoaderUtil.displayUserImage(getContext(), R.drawable.user_image_default, ivGuestAvatar, false, width, height, new ImageLoaderUtil.ImageLoaderCallback() {
            @Override
            public void onFailed(Exception e) {
                ivGuestAvatar.setImageResource(R.drawable.user_image_default);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                if (ivGuestAvatar != null)
                    ivGuestAvatar.setImageBitmap(BitmapUtil.blurImage(bitmap, 1f, 50));
            }
        });
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    public void updateState(@State int state) {
        Timber.e("updateState %d", state);
        if (mCurrentState == state) return;
//        if(mCurrentState==State.DISCONNECTED && state==State.DISCONNECTING) {
//            Timber.e("mCurrentState is disconnected but received disconnecting");
//            return;
//        }
        switch (state) {
            case State.ACCEPT:
                onAccept();
                break;
            case State.AUDIO_ONLY:
                ivGuestAvatar.setVisibility(VISIBLE);
                ivSwitchCam.setVisibility(GONE);
//                ivVideoControl.setImageResource(R.drawable.ic_vidcall_guest_enable_cam);
//                ivVideoControl.setMaxHeight(Utils.dpToPx(30));
                if (!rippleBackground.isRippleAnimationRunning())
                    rippleBackground.startRippleAnimation();
                rippleBackground.setVisibility(VISIBLE);
                tvCallStatus.setVisibility(GONE);
                tvUserName.setVisibility(VISIBLE);
                break;
            case State.VIDEO_AND_AUDIO:
                onConnected();
                break;
            case State.DISCONNECTING:
                onDisconnecting();
                break;
            case State.DISCONNECTED:
                initState();
                if (llCameraActionContainer.getVisibility() != VISIBLE)
                    llCameraActionContainer.setVisibility(VISIBLE);
                state = State.CONNECTING;
                break;
            default:
                break;
        }
        postInvalidate();
        mCurrentState = state;
        Timber.e("status ***** %d", state);
    }

    void onConnected() {
        if (mCurrentState == State.DISCONNECTING || mCurrentState == State.DISCONNECTED) {
            Timber.e("disconnecting state but call onConnected");
            return;
        }
        initState();
        if (flUserInfoContainer.getVisibility() != VISIBLE)
            flUserInfoContainer.setVisibility(VISIBLE);

    }

    private void onDisconnecting() {
        initState();
        if (llCameraActionContainer.getVisibility() != GONE)
            llCameraActionContainer.setVisibility(GONE);
        if (ibEndCall.getVisibility() == VISIBLE) ibEndCall.setVisibility(GONE);
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
        if (ivSwitchCam.getVisibility() == VISIBLE) ivSwitchCam.setVisibility(GONE);
        if (civAvatar.getVisibility() != VISIBLE) civAvatar.setVisibility(VISIBLE);
        if (!rippleBackground.isRippleAnimationRunning())
            rippleBackground.startRippleAnimation();
        rippleBackground.setVisibility(VISIBLE);
        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        if (flUserInfoContainer.getVisibility() == VISIBLE) flUserInfoContainer.setVisibility(GONE);
        if (tvUserName.getVisibility() != VISIBLE) tvUserName.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Disconnecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Disconnecting...");
    }

    private void onAccept() {
        initState();
        if (tvCountDown.getVisibility() != VISIBLE) tvCountDown.setVisibility(VISIBLE);
        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
        if (flUserInfoContainer.getVisibility() == VISIBLE) flUserInfoContainer.setVisibility(GONE);
        new CountDownAnimation(5000, 1000, tvCountDown, () -> {
            Timber.e("onCountDownCompleted");
            countDownCompleted();
        }).start();
    }

    private void countDownCompleted() {
        if (mListener != null) mListener.onCountDownCompleted();
    }

    public void streamStarted() {
        updateState(State.VIDEO_AND_AUDIO);
    }


    private void initState() {
        if (ivGuestAvatar.getVisibility() == VISIBLE) ivGuestAvatar.setVisibility(GONE);
        if (mVGuestAvatarOverlay.getVisibility() == VISIBLE)
            mVGuestAvatarOverlay.setVisibility(GONE);
        if (rippleBackground.isRippleAnimationRunning())
            rippleBackground.stopRippleAnimation();
        if (rippleBackground.getVisibility() == VISIBLE) rippleBackground.setVisibility(GONE);
        if (civAvatar.getVisibility() == VISIBLE) civAvatar.setVisibility(GONE);
        if (tvCallStatus.getVisibility() == VISIBLE) tvCallStatus.setVisibility(GONE);
        if (tvUserName.getVisibility() == VISIBLE) tvUserName.setVisibility(GONE);
        if (ivSwitchCam.getVisibility() == VISIBLE) ivSwitchCam.setVisibility(INVISIBLE);
        if (ibEndCall.getVisibility() == VISIBLE) ibEndCall.setVisibility(GONE);
    }

    public void showScreenOptions() {
        Timber.e("showScreenOptions");
        if (flUserInfoContainer.getVisibility() == VISIBLE)
            flUserInfoContainer.setVisibility(GONE);
        if (ibEndCall.getVisibility() != VISIBLE) ibEndCall.setVisibility(VISIBLE);
        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        if (ivSwitchCam.getVisibility() != VISIBLE) ivSwitchCam.setVisibility(VISIBLE);
        mHandler.removeCallbacks(dismissOptionRunable);
        mHandler.postDelayed(dismissOptionRunable, 2000);
    }


    @OnClick(R.id.ivSwitchCam)
    public void onIvSwitchCamClicked() {
        if (mListener != null) mListener.onSwitchCamClicked();
    }

//    @OnClick(ivVideoControl)
//    public void onIvVideoControlClicked() {
//        mEnableCamera = !mEnableCamera;
//        updateState(mEnableCamera ? State.VIDEO_AND_AUDIO : State.AUDIO_ONLY);
//        if (mListener != null) mListener.onAudioOnlyChecked(!mEnableCamera);
//    }


    @OnClick(R.id.flUserInfoContainer)
    public void onViewGuestProfile() {
        Timber.e("flUserInfoContainer");
        showScreenOptions();
//        if (mListener != null) mListener.onGuestDisplayNameClicked();
    }

    @OnClick(R.id.ibEndCall)
    public void onIbEndCallClicked() {
        if (mListener != null) mListener.onEndCallClicked();
    }

    private final static class WeakRunnable implements Runnable {
        private final WeakReference<SubStreamStatusView> mSubStreamStatusViewWeakReference;

        WeakRunnable(SubStreamStatusView view) {
            mSubStreamStatusViewWeakReference = new WeakReference<SubStreamStatusView>(view);
        }

        @Override
        public void run() {
            final SubStreamStatusView view = mSubStreamStatusViewWeakReference.get();
            if (view != null) view.onConnected();
        }
    }

    public interface OnClickListener {
        void onSwitchCamClicked();

        void onAudioOnlyChecked(boolean audioOnly);

        void onEndCallClicked();

        void onCountDownCompleted();

        void onGuestDisplayNameClicked();

        void onShowGuestProfile();
    }
}
