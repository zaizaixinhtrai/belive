package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/27/2016.
 */

public class LikedStreamUsersRequestModel extends BasePagingRequestModel {
    @SerializedName("Slug")
    @Expose
    private String mSlug;

    public LikedStreamUsersRequestModel(String  slug){
        super();
        this.mSlug =slug;
    }

}
