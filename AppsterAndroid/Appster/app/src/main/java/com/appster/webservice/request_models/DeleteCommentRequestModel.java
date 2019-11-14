package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 12/11/2017.
 */

public class DeleteCommentRequestModel {
    @SerializedName("CommentId")
    @Expose
    public int commentId;
}
