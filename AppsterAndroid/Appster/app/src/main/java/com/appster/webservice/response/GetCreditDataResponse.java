package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/29/2016.
 */

public class GetCreditDataResponse {
    @SerializedName("TotalBean") @Expose
    private long mTotalBean;
    @SerializedName("TotalGold") @Expose
    private long mTotalGold;
    @SerializedName("Status") @Expose
    private int mStatus;
    @SerializedName("TotalGoldFans") @Expose
    private long mTotalGoldFans;

    public long getTotalGoldFans() {
        return mTotalGoldFans;
    }

    public void setTotalGoldFans(long totalGoldFans) {
        mTotalGoldFans = totalGoldFans;
    }

    public long getTotalBean() {
        return mTotalBean;
    }

    public void setTotalBean(long TotalBean) {
        this.mTotalBean = TotalBean;
    }

    public long getTotalGold() {
        return mTotalGold;
    }

    public void setTotalGold(long TotalGold) {
        this.mTotalGold = TotalGold;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int Status) {
        this.mStatus = Status;
    }
}
