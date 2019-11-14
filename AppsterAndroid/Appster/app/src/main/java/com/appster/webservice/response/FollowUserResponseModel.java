package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/27/2015.
 */
public class FollowUserResponseModel {
    @SerializedName("FollowingCount") @Expose
    private int mFollowingCount;

    public int getFollowingCount() {
        return mFollowingCount;
    }

    public void setFollowingCount(int followingCount) {
        mFollowingCount = followingCount;
    }
}
