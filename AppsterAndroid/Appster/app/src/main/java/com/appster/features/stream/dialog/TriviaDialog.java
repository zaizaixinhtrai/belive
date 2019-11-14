package com.appster.features.stream.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.appster.R;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.trivia.TriviaTimer;
import com.appster.customview.trivia.TriviaTimerCircleAngleAnimation;

/**
 * Created by thanhbc on 3/7/18.
 */

public class TriviaDialog extends Dialog {
    View mDialogView;

    @TriviaDialogType
    private int mAlertType = TriviaDialogType.NORMAL;
    private Animation mModelInAnim, mModelOutAnim, mOverlayOutAnim;
    boolean mCloseFromCancel;

    private String mTitleText;
    private String mContentText;

    private String mConfirmText;
    private String mCancelText;

    private CustomFontTextView mDialogTitle, mDialogContent;
    private CustomFontButton mPositiveButton, mNegativeButton;
    private boolean mShowCancel = true;

    private OnTriviaDialogClickListener mCancelClickListener;
    private OnTriviaDialogClickListener mConfirmClickListener;
    private int mProgressDuration = 0;
    private TriviaTimer mCountDownTimer;
    protected TriviaTimerCircleAngleAnimation mAnimation;
    @DrawableRes
    int mCancelDrawable = -1;
    @DrawableRes
    int mConfirmDrawable = -1;
    @ColorInt
    int mConfirmTextColor = 0;
    @ColorInt
    int mCancelTextColor = 0;

    public TriviaDialog(@NonNull Context context) {
        this(context, TriviaDialogType.NORMAL);
    }

    public TriviaDialog(@NonNull Context context, @TriviaDialogType int type) {
        super(context, R.style.trivia_alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        this.mAlertType = type;
        mModelInAnim = AnimationUtils.loadAnimation(context, R.anim.trivia_dialog_in);
        mModelOutAnim = AnimationUtils.loadAnimation(context, R.anim.trivia_dialog_out);
        mModelOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mDialogView != null) {
                    mDialogView.setVisibility(View.GONE);
                    if (mAnimation != null) {
                        mAnimation.cancel();
                    }
                    mDialogView.post(() -> {
                        if (mCloseFromCancel) {
                            TriviaDialog.this.cancel();
                        } else {
                            TriviaDialog.this.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (getWindow() != null) {
                    WindowManager.LayoutParams wlp = getWindow().getAttributes();
                    wlp.alpha = 1 - interpolatedTime;
                    getWindow().setAttributes(wlp);
                }
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    public TriviaDialog setProgressDuration(int duration) {
        mProgressDuration = duration;
        return this;
    }

    public interface OnTriviaDialogClickListener {
        void onClick(TriviaDialog alertDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trivia_dialog);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mDialogTitle = findViewById(R.id.dialog_title);
        mDialogContent = findViewById(R.id.dialog_message);

        mPositiveButton = findViewById(R.id.ok);
        mNegativeButton = findViewById(R.id.cancel);
        mCountDownTimer = findViewById(R.id.countDownTimer);
        if (mCancelDrawable != -1) mNegativeButton.setBackgroundResource(mCancelDrawable);
        if (mConfirmDrawable != -1) mPositiveButton.setBackgroundResource(mConfirmDrawable);
        if (mConfirmTextColor != 0) mPositiveButton.setTextColor(mConfirmTextColor);
        if (mCancelTextColor != 0) mNegativeButton.setTextColor(mCancelTextColor);
        mNegativeButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        mNegativeButton.setOnClickListener(view -> {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(this);
            } else {
                dismissWithAnimation();
            }
        });

        mPositiveButton.setOnClickListener(view -> {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(this);
            } else {
                dismissWithAnimation();
            }
        });

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeDialogType(mAlertType, true);
    }

    public TriviaDialog setTitleText(String titleText) {
        mTitleText = titleText;
        if (mDialogTitle != null && mTitleText != null) {
            mDialogTitle.setText(mTitleText);
        }
        return this;
    }

    public TriviaDialog setContentText(String contentText) {
        mContentText = contentText;
        if (mDialogContent != null && mContentText != null) {
            mDialogContent.setText(mContentText);
        }
        return this;
    }

    public TriviaDialog setConfirmText(String confirmText) {
        mConfirmText = confirmText;
        if (mPositiveButton != null && mConfirmText != null) {
            mPositiveButton.setText(mConfirmText);
        }
        return this;
    }

    public TriviaDialog setCancelText(String cancelText) {
        mCancelText = cancelText;
        if (mNegativeButton != null && mCancelText != null) {
            mNegativeButton.setText(mCancelText);
        }
        return this;
    }

    public TriviaDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mNegativeButton != null) {
            mNegativeButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public TriviaDialog setCancelClickListener(OnTriviaDialogClickListener cancelClickListener) {
        mCancelClickListener = cancelClickListener;
        return this;
    }

    public TriviaDialog setConfirmClickListener(OnTriviaDialogClickListener confirmClickListener) {
        mConfirmClickListener = confirmClickListener;
        return this;
    }

    public TriviaDialog setCancelDrawable(@DrawableRes int cancelDrawable) {
        mCancelDrawable = cancelDrawable;
        return this;
    }

    public TriviaDialog setConfirmDrawable(@DrawableRes int confirmDrawable) {
        mConfirmDrawable = confirmDrawable;
        return this;
    }

    public TriviaDialog setConfirmTextColor(@ColorInt int confirmTextColor) {
        mConfirmTextColor = confirmTextColor;
        return this;
    }

    public TriviaDialog setCancelTextColor(@ColorInt int cancelTextColor) {
        mCancelTextColor = cancelTextColor;
        return this;
    }

    @Override
    protected void onStart() {
        if (mDialogView != null) {
            mDialogView.startAnimation(mModelInAnim);
        }
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    @Override
    public void dismiss() {
        if (mAnimation != null) {
            mAnimation.cancel();
        }
        super.dismiss();
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mPositiveButton.startAnimation(mOverlayOutAnim);
        mDialogView.startAnimation(mModelOutAnim);
    }

    @Override
    public void onBackPressed() {
    }

    public void changeDialogType(int alertType) {
        changeDialogType(alertType, false);
    }

    private void changeDialogType(@TriviaDialogType int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case TriviaDialogType.NORMAL:

                    break;
                case TriviaDialogType.OPTION:

                    break;
                case TriviaDialogType.PROGRESS:
                    mCountDownTimer.setVisibility(View.VISIBLE);
                    mDialogTitle.setVisibility(View.GONE);
                    mCountDownTimer.setDegreesUpTillPreFill(360);
                    // The arc will be of 360 degrees - a circle.
                    mAnimation = new TriviaTimerCircleAngleAnimation(mCountDownTimer, 0);
                    startCountDown(mProgressDuration);
                    break;
            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    private void startCountDown(int second) {
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation.setDuration((second * 1000));
        }
        if (mCountDownTimer != null) {
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mDialogView != null) {
                        mDialogView.setVisibility(View.GONE);
                        mDialogView.post(() -> {
                            if (mCloseFromCancel) {
                                TriviaDialog.this.cancel();
                            } else {
                                TriviaDialog.this.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mCountDownTimer.startAnimation(mAnimation);
        }
    }

    private void playAnimation() {

    }

    private void restore() {

    }

}
