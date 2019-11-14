package com.appster.utility;

import com.appster.models.UserModel;
import com.crashlytics.android.Crashlytics;

import org.jivesoftware.smack.util.StringUtils;

/**
 * Created by gaku on 6/7/17.
 */

public class CrashlyticsUtil {

    public static void setUser(UserModel user) {
        if (user != null) {

            String userName = user.getUserName();
            String userId = user.getUserId();

            if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userId)) {
                return;
            }

            Crashlytics.setUserName(userName);
            Crashlytics.setUserIdentifier(userId);
        }
    }

    public static void logException(Throwable th) {
        Crashlytics.logException(th);
    }
}
