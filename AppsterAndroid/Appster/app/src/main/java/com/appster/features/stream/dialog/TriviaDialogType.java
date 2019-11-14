package com.appster.features.stream.dialog;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.appster.features.stream.dialog.TriviaDialogType.NORMAL;
import static com.appster.features.stream.dialog.TriviaDialogType.OPTION;
import static com.appster.features.stream.dialog.TriviaDialogType.PROGRESS;

/**
 * Created by thanhbc on 3/7/18.
 */

@IntDef({NORMAL, PROGRESS, OPTION})
@Retention(RetentionPolicy.SOURCE)
public @interface TriviaDialogType {
    int NORMAL = 0;
    int PROGRESS = 1;
    int OPTION = 2;
}
