package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/29/2016.
 */

public class ReportStreamRequestModel {
    @SerializedName("Slug") @Expose
    private String mSlug;
    @SerializedName("Reason") @Expose
    private String mReason;

    public String getMessage() {
        return mReason;
    }

    public void setMessage(String message) {
        mReason = message;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        this.mSlug = slug;
    }
}
