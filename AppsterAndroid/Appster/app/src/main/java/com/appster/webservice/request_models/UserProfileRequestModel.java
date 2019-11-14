package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * Created by User on 9/19/2015.
 */
public class UserProfileRequestModel extends BasePagingRequestModel {
    @SerializedName("UserName") @Expose
    private String mUserName;
    @SerializedName("ProfileId") @Expose
    private int mProfileId;
    @SerializedName("ViewType") @Expose
    private int mViewType;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public int getUser_id() {
        return mProfileId;
    }

    public void setUser_id(int user_id) {
        this.mProfileId = user_id;
    }

    public void setUser_id(String user_id) {
        try {
            this.mProfileId = Integer.parseInt(user_id);
        }catch (NumberFormatException e){
            Timber.e(e);
            this.mProfileId = 0;
        }
    }

    public int getView() {
        return mViewType;
    }

    public void setView(int view) {
        this.mViewType = view;
    }
}
