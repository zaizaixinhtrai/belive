package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/16/2015.
 */
public class SetFollowUserRequestModel {
    @SerializedName("FollowUserId")
    private String mFollowUserId;
    @SerializedName("PrivatePassword")
    private String privatePassword ="";
    public void setFollow_user_id(String follow_user_id) {
        this.mFollowUserId = follow_user_id;
    }

    public void setPrivatePassword(String privatePassword) {
        this.privatePassword = privatePassword;
    }
}
