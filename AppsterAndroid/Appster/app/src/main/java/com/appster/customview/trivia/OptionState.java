package com.appster.customview.trivia;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 2/22/18.
 */

@IntDef({OptionState.CORRECT, OptionState.INCORRECT, OptionState.UNSELECTED, OptionState.ELIMINATED})
@Retention(RetentionPolicy.SOURCE)
public @interface OptionState {
    int CORRECT = 0;
    int INCORRECT = 1;
    int UNSELECTED = 2;
    int ELIMINATED = 3;
}
