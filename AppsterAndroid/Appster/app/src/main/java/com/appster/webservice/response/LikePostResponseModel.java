package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/2/2015.
 */
public class LikePostResponseModel {
    @SerializedName("LikeCount") @Expose
    private int mLikeCount;

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }
}
