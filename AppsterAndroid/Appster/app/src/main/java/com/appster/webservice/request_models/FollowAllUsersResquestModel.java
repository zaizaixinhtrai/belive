package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 9/19/2017.
 */

public class FollowAllUsersResquestModel {


    @SerializedName("FollowUserIds")
    @Expose
    private String mFollowUserIds;

    public FollowAllUsersResquestModel(String mFollowUserIds) {
        this.mFollowUserIds = mFollowUserIds;
    }

    public String getFollowUserIds() {
        return mFollowUserIds;
    }

    public void setFollowUserIds(String followUserIds) {
        this.mFollowUserIds = followUserIds;
    }
}
