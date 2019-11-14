package com.appster.customview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.appster.R;

/**
 * Created by linh on 06/09/2017.
 */

public class ShowCaseViewTutorial extends FrameLayout {
    private static final String TAG = ShowCaseViewTutorial.class.getName();
    OnShowCaseViewDismiss mOnShowCaseViewDismiss;
    boolean isDismissAnimation;

    public ShowCaseViewTutorial(@NonNull Context context) {
        super(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
        setBackgroundResource(R.color.transparent);
        setOnClickListener(v -> {
            if (isDismissAnimation) return;
            removeViewWithAnimation((ViewGroup) getParent(), ShowCaseViewTutorial.this);
            if (mOnShowCaseViewDismiss != null) mOnShowCaseViewDismiss.onDismiss();
        });
        setTag(TAG);
    }

    public ShowCaseViewTutorial(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowCaseViewTutorial(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShowCaseViewTutorial(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnShowCaseViewDismiss(OnShowCaseViewDismiss onShowCaseViewDismiss) {
        mOnShowCaseViewDismiss = onShowCaseViewDismiss;
    }

    public void show(Activity activity) {
        if (activity == null) return;
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        parent.addView(ShowCaseViewTutorial.this);
    }

    public static void removeItself(Activity activity) {
        if (activity == null) return;
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = parent.findViewWithTag(TAG);
        if (v != null) {
            parent.removeView(v);
        }

    }

    private void removeViewWithAnimation(ViewGroup parent, View view) {
        isDismissAnimation = true;
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parent.removeView(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public interface OnShowCaseViewDismiss {
        void onDismiss();
    }

    public static class Builder {
        BubbleSpeechTutorialTextView mBubbleSpeechTutorialTextView;
        ShowCaseViewTutorial mShowCaseViewTutorial;
        OnShowCaseViewDismiss listener;
        FrameLayout.LayoutParams mBubbleLps;
        private View mAnchorView;
        private boolean isVerticalArrow = true;
        private String mBubbleMessage;
        private int mBubbleMarginTopBottom = 0;
        private int mBubbleMarginLeftRight = 0;

        public Builder(Context context) {
            mShowCaseViewTutorial = new ShowCaseViewTutorial(context);
            mBubbleSpeechTutorialTextView = new BubbleSpeechTutorialTextView(context);
        }

        public Builder setBubbleSpeechTutorialTextView(BubbleSpeechTutorialTextView bubbleSpeechTutorialTextView) {
            mBubbleSpeechTutorialTextView = bubbleSpeechTutorialTextView;
            return this;
        }

        public Builder setBubbleLayoutParams(LayoutParams bubbleLps) {
            mBubbleLps = bubbleLps;
            return this;
        }

        public Builder setBubbleMarginTopBottom(int bubbleMarginTopBottom) {
            mBubbleMarginTopBottom = bubbleMarginTopBottom;
            return this;
        }

        public Builder setBubbleMarginLeftRight(int bubbleMarginLeftRight) {
            mBubbleMarginLeftRight = bubbleMarginLeftRight;
            return this;
        }

        public Builder setVerticalArrow(boolean verticalArrow) {
            isVerticalArrow = verticalArrow;
            return this;
        }

        public Builder setHorizontalArrow() {
            isVerticalArrow = false;
            return this;
        }

        public Builder setAnchorView(View anchorView) {
            mAnchorView = anchorView;
            return this;
        }

        public Builder setBubbleMessage(String bubbleMessage) {
            mBubbleMessage = bubbleMessage;
            return this;
        }

        public Builder setOnShowCaseViewDismiss(OnShowCaseViewDismiss listener) {
            this.listener = listener;
            return this;
        }

        public ShowCaseViewTutorial build() {
            if (mBubbleLps == null) {
                mBubbleLps = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            mBubbleSpeechTutorialTextView.setLayoutParams(mBubbleLps);
            mBubbleSpeechTutorialTextView.setAnchorView(mAnchorView);
            mBubbleSpeechTutorialTextView.setVerticalArrow(isVerticalArrow);
            mBubbleSpeechTutorialTextView.setText(mBubbleMessage);
            if (mBubbleMarginLeftRight > 0)
                mBubbleSpeechTutorialTextView.setBubbleMarginLeftRight(mBubbleMarginLeftRight);
            if (mBubbleMarginTopBottom > 0)
                mBubbleSpeechTutorialTextView.setBubbleMarginTopBottom(mBubbleMarginTopBottom);
            mShowCaseViewTutorial.addView(mBubbleSpeechTutorialTextView);
            mShowCaseViewTutorial.setOnShowCaseViewDismiss(listener);
            return mShowCaseViewTutorial;
        }
    }
}
