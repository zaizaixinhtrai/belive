package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/9/2015.
 */

public class AddCommentResponseModel {
    @SerializedName("CommentCount") @Expose
    private int mCommentCount;
    @SerializedName("CommentId") @Expose
    public int commentId;

    public int getCommentCount() {
        return mCommentCount;
    }

    public void setCommentCount(int commentCount) {
        mCommentCount = commentCount;
    }
}
