package com.appster.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.appster.R;
import com.appster.features.stream.CountDownAnimation;
import com.appster.features.stream.State;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by thanhbc on 1/12/18.
 */

public class IncomingCallView extends LinearLayout {

    WeakReference<ViewGroup> mParentView;
    @Bind(R.id.ivHostAvatar)
    CircleImageView mAvatar;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView mDisplayName;
    @Bind(R.id.tvCallState)
    AnimateTextView tvCallState;
    @Bind(R.id.tvCountDown)
    CustomFontTextView tvCountDown;
    protected Animation mSlideLeft;
    final int mAvatarSize = Utils.dpToPx(45);
    OnClickListener mListener;
    @State
    private int mCurrentState = State.CONNECTING;

    View mRootView;
    public IncomingCallView(Context context, ViewGroup parent,OnClickListener listener) {
        super(context);
        init(context, parent);
        this.mListener=listener;
    }

    private void init(Context context, ViewGroup parent) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.incoming_call_layout, this, true);
        ButterKnife.bind(mRootView);
        this.mParentView = new WeakReference<>(parent);
        mSlideLeft = AnimationUtils.loadAnimation(context.getApplicationContext(),
                R.anim.incoming_call_anim);
    }

    public void load(String profileLink, String displayName){
        mDisplayName.setText(displayName);
        tvCallState.setIndexBegin("Connecting...".indexOf(".") + 1);
        if(tvCallState!=null) tvCallState.animateText("Connecting...");
        final ViewGroup parent = mParentView.get();
        if (parent != null) parent.addView(this, 0);
        ImageLoaderUtil.displayUserImage(getContext(), profileLink, mAvatar, true, mAvatarSize, mAvatarSize, null);
    }

    public void updateState(@State int state) {
        if (mCurrentState == state) return;
        switch (state) {
            case State.ACCEPT:
                onAccept();
                break;
            default:
                break;
        }
    }

    private void onAccept() {
        if (tvCountDown!=null && tvCountDown.getVisibility() != VISIBLE) tvCountDown.setVisibility(VISIBLE);
        tvCallState.setTextAndStopAnimation("Ready in...");
        tvCallState.setIndexBegin("Ready in...".indexOf(".") + 1);
        if(tvCallState!=null) tvCallState.animateText("Ready in...");
        new CountDownAnimation(5000, 1000, tvCountDown, () -> {
            Timber.e("onCountDownCompleted");
            countDownCompleted();
        }, View.INVISIBLE).start();
    }

    private void countDownCompleted() {
        if (mListener != null) mListener.onCountDownCompleted();
        if(mParentView.get()!=null)  mParentView.get().removeView(this);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mSlideLeft!=null) startAnimation(mSlideLeft);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mSlideLeft!=null) mSlideLeft.cancel();
        ButterKnife.unbind(mRootView);
    }

    public interface OnClickListener {
        void onCountDownCompleted();
    }
}
