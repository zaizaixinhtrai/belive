package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 5/26/2016.
 */
public class ReportUserRequestModel {
    @SerializedName("ReportedUserId") @Expose
    private String mReportedUserId;
    @SerializedName("Reason") @Expose
    private String mReason;

    public String getReportedUserId() {
        return mReportedUserId;
    }

    public void setReportedUserId(String reportedUserId) {
        mReportedUserId = reportedUserId;
    }

    public String getReason() {
        return mReason;
    }

    public void setReason(String reason) {
        mReason = reason;
    }
}
