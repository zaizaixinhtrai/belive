package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/16/2015.
 */

public class VerifyIAppPayResponseModel {
    @SerializedName("PurchaseTime") @Expose
    private long mPurchaseTime;
    @SerializedName("TransactionId") @Expose
    private String mTransactionId;
    @SerializedName("TotalBean") @Expose
    private long mTotalBean;

    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    public String getTransactionId() {
        return mTransactionId;
    }

    public long getTotalBean() {
        return mTotalBean;
    }

    public void setTotalBean(long totalBean) {
        this.mTotalBean = totalBean;
    }
}
