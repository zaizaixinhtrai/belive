package com.appster.search;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Ngoc on 5/28/2018.
 */

@IntDef({SearchUserTypeItems.USER_ITEM, SearchUserTypeItems.FOOTER_VIEW})
@Retention(RetentionPolicy.SOURCE)
public @interface SearchUserTypeItems {
    int USER_ITEM = 0;
    int FOOTER_VIEW = 1;
}
