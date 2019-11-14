package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/27/2016.
 */

public class LikedUsersRequestModel extends BasePagingRequestModel {
    @SerializedName("PostId")
    @Expose
    private int mPostId;

    public LikedUsersRequestModel(int postId){
        super();
        mPostId=postId;
    }

}
