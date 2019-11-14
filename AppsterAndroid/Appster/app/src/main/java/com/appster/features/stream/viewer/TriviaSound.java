package com.appster.features.stream.viewer;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Ngoc on 3/26/2018.
 */

@IntDef({TriviaSound.QUESTIONS_SOUND, TriviaSound.CORRECT_SOUND, TriviaSound.WRONG_SOUND})
@Retention(RetentionPolicy.SOURCE)
public @interface TriviaSound {
    int QUESTIONS_SOUND = 0;
    int CORRECT_SOUND = 1;
    int WRONG_SOUND = 2;
}
