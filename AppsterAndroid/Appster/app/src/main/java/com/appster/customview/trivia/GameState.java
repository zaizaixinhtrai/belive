package com.appster.customview.trivia;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 2/25/18.
 */

@IntDef({GameState.ALIVE, GameState.ELIMINATED})
@Retention(RetentionPolicy.SOURCE)
public @interface GameState {
    int ELIMINATED = 0;
    int ALIVE = 1;
}
