package com.appster.webservice.request_models;

import com.apster.common.Constants.COMMENT_TYPE;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/8/2015.
 */
public class CommentListRequestModel extends BasePagingRequestModel {
    @SerializedName("PostId") @Expose
    private int mPostId;
    @SerializedName("streamId") @Expose
    private int mStreamId;
    @SerializedName("Type") @Expose @COMMENT_TYPE
    private int mType;

    public int getPostId() {
        return mPostId;
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public @COMMENT_TYPE int getType() {
        return mType;
    }

    public void setType(@COMMENT_TYPE int type) {
        mType = type;
    }
}
