package com.appster.customview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by thanhbc on 8/22/17.
 */

public class TopFanView extends LinearLayout {


    private static final int DELAYED_TIME_TO_DETACH = 3000;
    private static final int DURATION_SLIDE_ANIMATION = 500;
    private static final String TRANLATION_X = "translationX";
    private CircleImageView mAvatar;
    private CustomFontTextView mDisplayName;
    final int mAvatarSize = Utils.dpToPx(45);
    private Runnable mRunnableSlideOut;
    WeakReference<ViewGroup> mParentView;

    public TopFanView(Context context, ViewGroup parent) {
        super(context);
        init(context, parent);
    }


    void init(Context context, ViewGroup parent) {
        LayoutInflater.from(context).inflate(R.layout.topfan_view, this, true);
        mAvatar = (CircleImageView) findViewById(R.id.ivTopFanAvatar);
        mDisplayName = (CustomFontTextView) findViewById(R.id.tvDisplayName);
        this.mParentView = new WeakReference<>(parent);
        mRunnableSlideOut = this::detachAnimation;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachAnimation();
        postDelayed(mRunnableSlideOut, DELAYED_TIME_TO_DETACH);
    }

    public void load(String profileLink, String displayName, @DrawableRes int rankDrawable) {
        ImageLoaderUtil.displayUserImage(getContext(), profileLink, mAvatar, true, mAvatarSize, mAvatarSize, null);

        Drawable d = ContextCompat.getDrawable(getContext(), rankDrawable);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        mDisplayName.setCompoundDrawables(d, null, null, null);
        mDisplayName.setCompoundDrawablePadding(Utils.dpToPx(2));
        mDisplayName.setText(displayName);
        final ViewGroup parent = mParentView.get();
        if (parent != null) parent.addView(this, 0);
    }

    private void detachAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, TRANLATION_X, 0f, -getWidth());
        objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                TopFanView.this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                final ViewGroup parent = mParentView.get();
                if (parent != null) parent.removeView(TopFanView.this);
                TopFanView.this.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //do not thing
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                //do not thing
            }
        });
    }

    public void attachAnimation() {
        int translate = Utils.dpToPx(150);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, TRANLATION_X, -translate, 0f);
        objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }


}
