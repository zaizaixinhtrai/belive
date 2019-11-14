package com.appster.features.home;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 5/21/18.
 */
@IntDef({ShowStatus.WAITING, ShowStatus.WATCHING, ShowStatus.STARTING, ShowStatus.PLAY, ShowStatus.FINISHED})
@Retention(RetentionPolicy.SOURCE)
public @interface ShowStatus {
    int WAITING = 1;
    int WATCHING = 4;
    int STARTING = 2;
    int PLAY = 3;
    int FINISHED = 5;
}
