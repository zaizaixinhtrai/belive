package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 7/19/2016.
 */
public class VerifyUsernameRequestModel {
    @SerializedName("UserName") @Expose
    private String mUserName;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }
}
