package com.appster.features.stream;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 9/7/17.
 */

@IntDef({Role.HOST, Role.GUEST, Role.VIEWER})
@Retention(RetentionPolicy.SOURCE)
public @interface Role {
    int HOST = 1;
    int GUEST = 2;
    int VIEWER = 3;
}