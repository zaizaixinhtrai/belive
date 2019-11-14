package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/2/2015.
 */
public class LikePostRequestModel {
    @SerializedName("Like") @Expose
    private boolean mLike;
    @SerializedName("PostId") @Expose
    private String mPostId;

    public boolean getLike() {
        return mLike;
    }

    public void setLike(boolean like) {
        this.mLike = like;
    }

    public String getPost_id() {
        return mPostId;
    }

    public void setPost_id(String post_id) {
        this.mPostId = post_id;
    }

}
