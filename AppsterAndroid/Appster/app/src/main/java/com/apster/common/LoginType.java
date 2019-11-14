package com.apster.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.apster.common.LoginType.LOGIN_FROM_EMAIL;
import static com.apster.common.LoginType.LOGIN_FROM_FACEBOOK;
import static com.apster.common.LoginType.LOGIN_FROM_GOOGLE;
import static com.apster.common.LoginType.LOGIN_FROM_INSTAGRAM;
import static com.apster.common.LoginType.LOGIN_FROM_PHONE;
import static com.apster.common.LoginType.LOGIN_FROM_PLAY_TOKEN;
import static com.apster.common.LoginType.LOGIN_FROM_TWITTER;

/**
 * Created by linh on 20/09/2017.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({LOGIN_FROM_FACEBOOK, LOGIN_FROM_PLAY_TOKEN, LOGIN_FROM_INSTAGRAM, LOGIN_FROM_GOOGLE, LOGIN_FROM_TWITTER, LOGIN_FROM_EMAIL, LOGIN_FROM_PHONE})
public @interface LoginType {
    int LOGIN_FROM_FACEBOOK = 0;
    int LOGIN_FROM_PLAY_TOKEN = 1;
    int LOGIN_FROM_INSTAGRAM = 2;
    int LOGIN_FROM_GOOGLE = 3;
    int LOGIN_FROM_TWITTER = 4;
    int LOGIN_FROM_EMAIL = 5;
    int LOGIN_FROM_PHONE = 6;
}
