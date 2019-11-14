package com.appster.webservice.response;


import com.appster.models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/24/2015.
 */
public class LoginResponseModel {
    public static final int LOGIN_FROM_FACEBOOK = 0;
//    public static final int LOGIN_FROM_PLAY_TOKEN = 1;
//    public static final int LOGIN_FROM_INSTAGRAM = 2;
    public static final int LOGIN_FROM_GOOGLE = 3;
//    public static final int LOGIN_FROM_TWITTER = 4;
//    public static final int LOGIN_FROM_MAIL = 5;
//    public static final int LOGIN_FROM_PHONE = 6;

    @SerializedName("UserInfo") @Expose
    private UserModel mUserInfo;
    @SerializedName("AccessToken") @Expose
    private String mAccessToken;
    @SerializedName("RefreshToken") @Expose
    private String mRefreshToken;
    @SerializedName("Expires") @Expose
    private String mExpires;
    @SerializedName("LoginType") @Expose
    private int mLoginType;

    public UserModel getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserModel userInfo) {
        mUserInfo = userInfo;
    }

    public String getAccess_token() {
        return mAccessToken;
    }

    public void setAccess_token(String access_token) {
        this.mAccessToken = access_token;
    }

    public String getRefresh_token() {
        return mRefreshToken;
    }

    public void setRefresh_token(String refresh_token) {
        this.mRefreshToken = refresh_token;
    }

    public String getExpires() {
        return mExpires;
    }

    public void setExpires(String expires) {
        this.mExpires = expires;
    }

    public int getLoginType() {
        return mLoginType;
    }

//    @IntDef({LOGIN_FROM_FACEBOOK, LOGIN_FROM_PLAY_TOKEN, LOGIN_FROM_INSTAGRAM, LOGIN_FROM_GOOGLE, LOGIN_FROM_TWITTER, LOGIN_FROM_MAIL, LOGIN_FROM_PHONE})
//    @Retention(RetentionPolicy.SOURCE)
//    @interface LoginFrom{}
}
