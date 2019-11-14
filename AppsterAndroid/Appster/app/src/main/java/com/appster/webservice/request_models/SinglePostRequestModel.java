package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/11/2015.
 */
public class SinglePostRequestModel {
    @SerializedName("PostId") @Expose
    private String mPostId;

    public String getPost_id() {
        return mPostId;
    }

    public void setPost_id(String post_id) {
        this.mPostId = post_id;
    }
}
