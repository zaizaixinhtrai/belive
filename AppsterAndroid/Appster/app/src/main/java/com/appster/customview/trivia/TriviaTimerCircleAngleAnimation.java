package com.appster.customview.trivia;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaTimerCircleAngleAnimation extends Animation {

    private TriviaTimer circle;

    private float startingAngle;
    private float endingAngle;
    private int remainSecs = 0;

    public TriviaTimerCircleAngleAnimation(TriviaTimer circle, int endingAngle) {
        this.startingAngle = circle.getDegreesUpTillPreFill();
        this.endingAngle = endingAngle;
        this.circle = circle;
        setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime,
                                       Transformation transformation) {

        if (circle != null) {
            float finalAngle = startingAngle + ((endingAngle - startingAngle)
                    * interpolatedTime);

            circle.setDegreesUpTillPreFill(finalAngle);
            int temp = (int) ((1 - interpolatedTime) * (getDuration() / 1000));
            if (remainSecs != temp) {
                remainSecs = temp;
            }
            circle.setText(String.valueOf(remainSecs + 1));
            circle.requestLayout();
        }
    }
}
