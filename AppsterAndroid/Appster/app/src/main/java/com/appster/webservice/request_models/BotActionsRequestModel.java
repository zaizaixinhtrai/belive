package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 12/10/2016.
 */

public class BotActionsRequestModel {
    @SerializedName("Slug") @Expose
    private String mSlug;
    @SerializedName("UserIds") @Expose
    private String mUserIds;
    @SerializedName("ActionType") @Expose
    private int mActionType;

    public BotActionsRequestModel(String slug, String userIds, int actionType) {
        mSlug = slug;
        mUserIds = userIds;
        mActionType = actionType;
    }

    public String getSlug() {
        return mSlug;
    }

    public String getUserIds() {
        return mUserIds;
    }

    public int getActionType() {
        return mActionType;
    }
}
