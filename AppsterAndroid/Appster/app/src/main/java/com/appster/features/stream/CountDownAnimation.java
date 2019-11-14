package com.appster.features.stream;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by thanhbc on 9/9/17.
 */

public class CountDownAnimation extends CountDownTimer {
    private final WeakReference<TextView> mTextViewWeakReference;
    private OnCountDownFinished mOnCountDownFinished;
    private Animation mAnimation;
    private int mVisibilityAfterCompleted = View.GONE;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownAnimation(long millisInFuture, long countDownInterval, TextView textView, OnCountDownFinished callback) {
        super(millisInFuture, countDownInterval);
        mTextViewWeakReference = new WeakReference<>(textView);
        mOnCountDownFinished = callback;
        mVisibilityAfterCompleted = View.GONE;
//        mAnimation = new AlphaAnimation(1.0f, 0.0f);
//        mAnimation.setDuration(1000);
    }

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownAnimation(long millisInFuture, long countDownInterval, TextView textView, OnCountDownFinished callback,int visibilityAfterCompleted) {
        super(millisInFuture, countDownInterval);
        mTextViewWeakReference = new WeakReference<>(textView);
        mOnCountDownFinished = callback;
        mVisibilityAfterCompleted = visibilityAfterCompleted;
//        mAnimation = new AlphaAnimation(1.0f, 0.0f);
//        mAnimation.setDuration(1000);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        final TextView textView = mTextViewWeakReference.get();
        if (textView != null) {
            int value = (int) millisUntilFinished / 1000;
            if(value > 4) return;
            if(value - 1 == 0) {
                textView.setText("0");
                textView.setVisibility(mVisibilityAfterCompleted);
                if (mOnCountDownFinished != null){
                    mOnCountDownFinished.onFinished();
                    mOnCountDownFinished=null;
                }
            }else{
                textView.setText(String.valueOf(value-1));
            }
//            textView.startAnimation(mAnimation);
        }
    }

    @Override
    public void onFinish() {
    }

    public interface OnCountDownFinished{
        void onFinished();
    }
}
