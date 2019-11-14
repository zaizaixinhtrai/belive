package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/28/2015.
 */
public class FollowRequestModel extends BasePagingRequestModel{
    @SerializedName("ProfileId") @Expose
    private String mProfileId;

    public String getProfile_id() {
        return mProfileId;
    }

    public void setProfile_id(String profile_id) {
        this.mProfileId = profile_id;
    }
}
