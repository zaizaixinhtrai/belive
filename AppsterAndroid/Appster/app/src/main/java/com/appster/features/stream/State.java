package com.appster.features.stream;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 9/7/17.
 */
@IntDef({State.ACCEPT, State.REJECT, State.CONNECTING, State.AWAY, State.NO_ANSWER,
        State.AUDIO_ONLY, State.CONNECTED, State.DISCONNECTING, State.DISCONNECTED, State.VIDEO_AND_AUDIO,State.RECONNECTED})
@Retention(RetentionPolicy.SOURCE)
public @interface State {
    int ACCEPT = 0;
    int REJECT = 1;
    int CONNECTING = 2;
    int CONNECTED = 3;
    int AWAY = 4;
    int NO_ANSWER = 5;
    int AUDIO_ONLY = 6;
    int DISCONNECTING = 7;
    int DISCONNECTED = 8;
    int VIDEO_AND_AUDIO = 9;
    int RECONNECTED = 10;
}