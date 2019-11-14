package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/16/2015.
 */
public class SetUnfollowUserRequestModel {
    @SerializedName("FollowUserId") @Expose
    private String mFollowUserId;

    public String getFollow_user_id() {
        return mFollowUserId;
    }

    public void setFollow_user_id(String follow_user_id) {
        this.mFollowUserId = follow_user_id;
    }

}
