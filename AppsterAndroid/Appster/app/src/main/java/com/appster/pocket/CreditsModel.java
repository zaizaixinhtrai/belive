package com.appster.pocket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/16/2015.
 */
public class CreditsModel {
    @SerializedName("Status")
    @Expose
    private int Status;
    @SerializedName("TotalBean")
    @Expose
    private long TotalBean;
    @SerializedName("TotalGold")
    @Expose
    private long TotalGold;
    @SerializedName("TotalGoldFans")
    @Expose
    private long TotalGoldFans;

    @SerializedName("TotalPoint")
    @Expose
    public int totalPoint;
    @SerializedName("PointInfoUrl")
    @Expose
    private String mPointInfoUrl;

    public long getTotalGoldFans() {
        return TotalGoldFans;
    }

    public void setTotalGoldFans(long totalGoldFans) {
        TotalGoldFans = totalGoldFans;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public long getTotal_bean() {
        return TotalBean;
    }

    public void setTotal_bean(int total_bean) {
        this.TotalBean = total_bean;
    }

    public long getTotal_gold() {
        return TotalGold;
    }

    public void setTotal_gold(int total_gold) {
        this.TotalGold = total_gold;
    }

    public String getPointInfoUrl() {
        return mPointInfoUrl;
    }
}
