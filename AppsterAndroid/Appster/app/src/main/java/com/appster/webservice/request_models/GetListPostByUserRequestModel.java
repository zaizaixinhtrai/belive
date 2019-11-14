package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 6/14/2016.
 */
public class GetListPostByUserRequestModel extends BasePagingRequestModel {
    @SerializedName("UserName") @Expose
    private String mUserName;
    @SerializedName("ProfileId") @Expose
    private int mProfileId;
    @SerializedName("ViewType") @Expose
    private int mViewType;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String UserName) {
        this.mUserName = UserName;
    }

    public int getProfileId() {
        return mProfileId;
    }

    public void setProfileId(int ProfileId) {
        this.mProfileId = ProfileId;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int ViewType) {
        this.mViewType = ViewType;
    }
}
