package com.appster.customview.trivia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.appster.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 2/22/18.
 */

public class TriviaOption extends FrameLayout {
    @Bind(R.id.pgView)
    ProgressBar pgView;
    @Bind(R.id.tvOptionText)
    TextView tvOptionText;
    @Bind(R.id.tvChosenNum)
    TextView tvChosenNum;
    ProgressBarAnimation mProgressBarAnimation;

    public TriviaOption(@NonNull Context context) {
        this(context, null);
    }

    public TriviaOption(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (getContext() == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.trivia_option_view, this, true);
        ButterKnife.bind(this, view);
        mProgressBarAnimation = new ProgressBarAnimation(pgView);
    }

    public void updateState(@OptionState int state, int totalNum, float selectedNum) {
        int percent = 0;
        if (totalNum > 0) {
            percent = (int) ((selectedNum / totalNum) * 100);
        }

        tvOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_58585b));
        tvChosenNum.setText(String.valueOf((int) selectedNum));
        tvChosenNum.setVisibility(VISIBLE);
        pgView.setProgressDrawable(getStateDrawable(state));
        mProgressBarAnimation.setProgress(percent);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        updatePgViewState(selected ? R.drawable.trivia_state_selected : R.drawable.trivia_state_normal);
        tvOptionText.setTextColor(ContextCompat.getColor(getContext(), selected ? R.color.white : R.color.color_58585b));
    }

    public void updatePgViewState(@DrawableRes int state) {
        if (pgView != null && getContext() != null)
            pgView.setProgressDrawable(ContextCompat.getDrawable(getContext(), state));
    }

    public void resetState() {
        tvChosenNum.setVisibility(GONE);
        setSelected(false);
    }

    private Drawable getStateDrawable(@OptionState int i) {
        int drawable = R.drawable.trivia_state_normal;
        switch (i) {
            case OptionState.CORRECT:
                drawable = R.drawable.trivia_state_correct;
                break;
            case OptionState.INCORRECT:
                drawable = R.drawable.trivia_state_incorrect;
                break;
            case OptionState.UNSELECTED:
                drawable = R.drawable.trivia_state_unselected;
                break;
            default:
                break;
        }
        return ContextCompat.getDrawable(getContext(), drawable);
    }

    public void setOption(String option) {
        tvOptionText.setText(option);
    }

    public void updateEliminatedState() {
        if (pgView != null && getContext() != null)
            pgView.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trivia_state_eliminated));
        tvOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_9b9b9b));
    }
}
