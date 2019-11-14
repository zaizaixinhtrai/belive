package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/4/2015.
 */
public class GetBeanHistoryRequestModel {
    @SerializedName("ToDate") @Expose
    private String mToDate;
    @SerializedName("FromDate") @Expose
    private String mFromDate;
    @SerializedName("user_id") @Expose
    private String mUserId;
    @SerializedName("NextId") @Expose
    private int mNextId;
    @SerializedName("Limit") @Expose
    private int mLimit;

    public String getTo_date() {
        return mToDate;
    }

    public void setTo_date(String to_date) {
        this.mToDate = to_date;
    }

    public String getFrom_date() {
        return mFromDate;
    }

    public void setFrom_date(String from_date) {
        this.mFromDate = from_date;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public int getIndex() {
        return mNextId;
    }

    public void setIndex(int index) {
        this.mNextId = index;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        this.mLimit = limit;
    }
}
