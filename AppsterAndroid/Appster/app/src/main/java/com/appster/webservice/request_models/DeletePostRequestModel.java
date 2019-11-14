package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/26/2015.
 */
public class DeletePostRequestModel {
    @SerializedName("PostId") @Expose
    private String mPostId;

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String post_id) {
        this.mPostId = post_id;
    }
}
