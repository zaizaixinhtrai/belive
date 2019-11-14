package com.appster.webservice.request_models;

import com.apster.common.Constants.COMMENT_TYPE;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 10/9/2015.
 */
public class AddCommentRequestModel {
    @SerializedName("Message") @Expose
    private String mMessage;
    @SerializedName("PostId") @Expose
    private int mPostId;
    @SerializedName("CommentId") @Expose
    private String mCommentId;
    @SerializedName("streamId") @Expose
    private int mStreamId;
    @SerializedName("Type") @Expose @COMMENT_TYPE
    private int mType;
    @SerializedName("TagUsers") @Expose
    private String mTaggedUsers;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getPostId() {
        return mPostId;
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public void setCommentId(String commentId) {
        mCommentId = commentId;
    }

    public int getStreamId() {
        return mStreamId;
    }

    public void setStreamId(int streamId) {
        mStreamId = streamId;
    }

    public int getType() {
        return mType;
    }

    public void setType(@COMMENT_TYPE int type) {
        mType = type;
    }

    public String getTaggedUsers() {
        return mTaggedUsers;
    }

    public void setTaggedUsers(String taggedUsers) {
        mTaggedUsers = taggedUsers;
    }
}
