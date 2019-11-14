package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sonnguyen on 11/21/16.
 */

public class ViewVideosRequestModel {
    @SerializedName("PostId") @Expose
    private String mPostId;

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        mPostId = postId;
    }
    public ViewVideosRequestModel(String postID){
        setPostId(postID);
    }
}
