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
import com.appster.features.stream.State;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;
import com.pack.RSBlur;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 1/12/18.
 */

public class GuestSubStreamStatusView extends FrameLayout {
    @Bind(R.id.ivHostAvatar)
    ImageView ivGuestAvatar;

    @Bind(R.id.ivGuestCircleAvatar)
    CircleImageView ivGuestCircleAvatar;
    @Bind(R.id.ibEndCall)
    ImageButton ibEndCall;
    @Bind(R.id.ivSwitchCam)
    ImageView ivSwitchCam;
//    @Bind(R.id.ivVideoControl)
//    ImageView ivVideoControl;

    @Bind(R.id.tvCallStatus)
    AnimateTextView tvCallStatus;

    @Bind(R.id.tvDisplayName)
    CustomFontTextView tvDisplayName;

    private boolean mEnableCamera = true;
    @Bind(R.id.vGuestAvatarOverlay)
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

    public GuestSubStreamStatusView(Context context) {
        super(context);
        init(context);
    }

    public GuestSubStreamStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sub_camera_state_new, this, true);
        ButterKnife.bind(this);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ivGuestAvatar.post(this::loadGuestImage);

    }

    public void setGuestName(String displayName) {
        mGuestDisplayName = displayName;
        tvDisplayName.setText(mGuestDisplayName);
    }

    public void setGuestAvatar(String avatarUrl) {
        mGuestAvatarLink = avatarUrl;
        loadGuestImage();
        ImageLoaderUtil.displayUserImage(getContext(), mGuestAvatarLink, ivGuestCircleAvatar);
    }

    private void loadGuestImage() {
        if (mGuestAvatarLink.isEmpty()) {
            loadDefaultGuestImage();
        } else {
            ImageLoaderUtil.displayUserImage(getContext(), mGuestAvatarLink, ivGuestAvatar, true, 0, 0, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    if(ivGuestAvatar!=null) ivGuestAvatar.setImageResource(R.drawable.user_image_default);
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (ivGuestAvatar != null && getContext()!=null)
                        ivGuestAvatar.setImageBitmap(RSBlur.blur(getContext(),bitmap, 25));
                }
            });
        }
    }

    private void loadDefaultGuestImage() {

        ImageLoaderUtil.displayUserImage(getContext(), R.drawable.user_image_default, ivGuestAvatar, false, 0, 0, new ImageLoaderUtil.ImageLoaderCallback() {
            @Override
            public void onFailed(Exception e) {
                if(ivGuestAvatar!=null) ivGuestAvatar.setImageResource(R.drawable.user_image_default);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                if (ivGuestAvatar != null && getContext()!=null)
                    ivGuestAvatar.setImageBitmap(RSBlur.blur(getContext(),bitmap, 25));
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
                tvCallStatus.setVisibility(GONE);
                break;
            case State.VIDEO_AND_AUDIO:
                onConnected();
                break;
            case State.DISCONNECTING:
                onDisconnecting();
                break;
            case State.DISCONNECTED:
                initState();
                state = State.CONNECTING;
                setVisibility(GONE);
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
    }

    private void onDisconnecting() {
        initState();
        if (ibEndCall.getVisibility() == VISIBLE) ibEndCall.setVisibility(GONE);
        if (tvCallStatus.getVisibility() != VISIBLE) tvCallStatus.setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
        if (ivSwitchCam.getVisibility() == VISIBLE) ivSwitchCam.setVisibility(GONE);
        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
            mVGuestAvatarOverlay.setVisibility(VISIBLE);
        tvCallStatus.setIndexBegin("Disconnecting...".indexOf(".") + 1);
        tvCallStatus.animateText("Disconnecting...");
    }

    private void onAccept() {
        initState();
        if (getVisibility() != INVISIBLE) setVisibility(INVISIBLE);
//        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
//            mVGuestAvatarOverlay.setVisibility(VISIBLE);
//        if (ivGuestAvatar.getVisibility() != VISIBLE) ivGuestAvatar.setVisibility(VISIBLE);
//        postDelayed(this::countDownCompleted,2000);
    }

    private void countDownCompleted() {
        if (mListener != null) mListener.onCountDownCompleted();
    }

    public void streamStarted() {
        updateState(State.VIDEO_AND_AUDIO);
    }


    private void initState() {
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);
        if (ivGuestAvatar.getVisibility() == VISIBLE) ivGuestAvatar.setVisibility(GONE);
        if (mVGuestAvatarOverlay.getVisibility() == VISIBLE)
            mVGuestAvatarOverlay.setVisibility(GONE);
        if (tvCallStatus.getVisibility() == VISIBLE) tvCallStatus.setVisibility(GONE);
        if (ibEndCall.getVisibility() != VISIBLE) ibEndCall.setVisibility(VISIBLE);
        if (ivSwitchCam.getVisibility() != VISIBLE) ivSwitchCam.setVisibility(VISIBLE);
    }

    public void showScreenOptions() {
        Timber.e("showScreenOptions");
//        if (ibEndCall.getVisibility() != VISIBLE) ibEndCall.setVisibility(VISIBLE);
//        if (mVGuestAvatarOverlay.getVisibility() != VISIBLE)
//            mVGuestAvatarOverlay.setVisibility(VISIBLE);
//        if (ivSwitchCam.getVisibility() != VISIBLE) ivSwitchCam.setVisibility(VISIBLE);
//        mHandler.removeCallbacks(dismissOptionRunable);
//        mHandler.postDelayed(dismissOptionRunable, 2000);
    }


    @OnClick(R.id.ivSwitchCam)
    public void onIvSwitchCamClicked(View v) {
        AppsterUtility.temporaryLockView(v);
        if (mListener != null) mListener.onSwitchCamClicked();
    }

//    @OnClick(ivVideoControl)
//    public void onIvVideoControlClicked() {
//        mEnableCamera = !mEnableCamera;
//        updateState(mEnableCamera ? State.VIDEO_AND_AUDIO : State.AUDIO_ONLY);
//        if (mListener != null) mListener.onAudioOnlyChecked(!mEnableCamera);
//    }


//    @OnClick(flUserInfoContainer)
//    public void onViewGuestProfile() {
//        Timber.e("flUserInfoContainer");
//        showScreenOptions();
////        if (mListener != null) mListener.onGuestDisplayNameClicked();
//    }

    @OnClick(R.id.ibEndCall)
    public void onIbEndCallClicked(View v) {
        AppsterUtility.temporaryLockView(v);
        if (mListener != null) mListener.onEndCallClicked();
    }

    private final static class WeakRunnable implements Runnable {
        private final WeakReference<GuestSubStreamStatusView> mGuestSubStreamStatusViewWeakReference;

        WeakRunnable(GuestSubStreamStatusView view) {
            mGuestSubStreamStatusViewWeakReference = new WeakReference<GuestSubStreamStatusView>(view);
        }

        @Override
        public void run() {
            final GuestSubStreamStatusView view = mGuestSubStreamStatusViewWeakReference.get();
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
