package com.appster.customview.trivia;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 2/24/18.
 */

@IntDef({TriviaGameState.GAME_START, TriviaGameState.GAME_FINISH, TriviaGameState.GAME_END, TriviaGameState.QUESTION_ANSWER_TIMESUP,
        TriviaGameState.RESULT_WAITING_TIMESUP, TriviaGameState.RESULT_TIMESUP, TriviaGameState.QUESTION_WAITING_TIMESUP,TriviaGameState.FINISH_WAITING_TIME})
@Retention(RetentionPolicy.SOURCE)
public @interface TriviaGameState {
    int GAME_START = -1;
    int QUESTION_ANSWER_TIMESUP = 0;
    int RESULT_WAITING_TIMESUP = 1;
    int RESULT_TIMESUP = 2;
    int QUESTION_WAITING_TIMESUP = 3;
    int GAME_FINISH = -2;
    int GAME_END = -3;
    int FINISH_WAITING_TIME = 4;
}
