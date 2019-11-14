package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/29/2015.
 */
public class ReportRequestModel {
    @SerializedName("PostId") @Expose
    private String mPostId;
    @SerializedName("Reason") @Expose
    private String mReason;

    public String getMessage() {
        return mReason;
    }

    public void setMessage(String message) {
        mReason = message;
    }

    public String getReport_for() {
        return mPostId;
    }

    public void setReport_for(String report_for) {
        this.mPostId = report_for;
    }
}
