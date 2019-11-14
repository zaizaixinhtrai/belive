package com.appster.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.appster.R;
import com.appster.features.stream.State;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.pack.RSBlur;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 1/16/18.
 */

public class HostSubStreamStatusView extends FrameLayout {

    @Bind(R.id.vGradientView)
    View vGradientView;
    @Bind(R.id.ivHostAvatar)
    ImageView ivHostAvatar;
    OnClickListener mListener;
    @State
    private int mCurrentState = State.CONNECTING;


    private String mHostAvatarUrl = "";

    public HostSubStreamStatusView(Context context) {
        super(context);
        init(context);
    }

    public HostSubStreamStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vid_host_cam_state, this, true);
        ButterKnife.bind(this);
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    public void setHostAvatarUrl(String hostAvatarUrl) {
        mHostAvatarUrl = hostAvatarUrl;
        loadHostImage();
    }

    private void loadHostImage() {
        if (mHostAvatarUrl.isEmpty()) {
            loadDefaultImage();
        } else {
            ImageLoaderUtil.displayUserImage(getContext(), mHostAvatarUrl, ivHostAvatar, true, 0, 0, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    if (ivHostAvatar != null)
                        ivHostAvatar.setImageResource(R.drawable.user_image_default);
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (ivHostAvatar != null && getContext() != null)
                        ivHostAvatar.setImageBitmap(RSBlur.blur(getContext(), bitmap, 25));
                }
            });
        }
    }

    private void loadDefaultImage() {

        ImageLoaderUtil.displayUserImage(getContext(), R.drawable.user_image_default, ivHostAvatar, false, 0, 0, new ImageLoaderUtil.ImageLoaderCallback() {
            @Override
            public void onFailed(Exception e) {
                if (ivHostAvatar != null)
                    ivHostAvatar.setImageResource(R.drawable.user_image_default);
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                if (ivHostAvatar != null && getContext() != null)
                    ivHostAvatar.setImageBitmap(RSBlur.blur(getContext(), bitmap, 25));
            }
        });
    }

    @OnClick(R.id.vGradientView)
    public void onViewClicked(View v) {
        AppsterUtility.temporaryLockView(v);
        if (mListener != null) mListener.onHostGradientViewClicked();
    }

    public void updateState(@State int state) {
        Timber.e("updateState %d", state);
        if (mCurrentState == state) return;
        switch (state) {
            case State.ACCEPT:
                if (getVisibility() != INVISIBLE) setVisibility(INVISIBLE);
                break;
            case State.CONNECTED:
                onConnected();
                break;
            case State.DISCONNECTING:
                onDisconnecting();
                break;
            case State.DISCONNECTED:
                initState();
                state = State.CONNECTING;
                setVisibility(GONE);
            default:
                break;
        }
        postInvalidate();
        mCurrentState = state;
    }

    private void onDisconnecting() {
        initState();
        if (ivHostAvatar.getVisibility() != VISIBLE) ivHostAvatar.setVisibility(VISIBLE);
    }

    void onConnected() {
        if (mCurrentState == State.DISCONNECTING || mCurrentState == State.DISCONNECTED) {
            Timber.e("disconnecting state but call onConnected");
            return;
        }
        initState();
    }

    private void initState() {
        if (ivHostAvatar.getVisibility() == VISIBLE) ivHostAvatar.setVisibility(GONE);
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);
    }

    public interface OnClickListener {
        void onHostGradientViewClicked();
    }
}
