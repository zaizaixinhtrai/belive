package com.apster.common;

import com.appster.activity.BaseActivity;

import static com.appster.utility.AppsterUtility.isAppOwner;

/**
 * Created by linh on 29/06/2017.
 */

public class NavigationUtil {
    public static void gotoProfileScreen(BaseActivity activity, String userName) {
        if (isAppOwner(userName)){
            return;
        }
        activity.startActivityProfile("", userName, "");
    }
}
