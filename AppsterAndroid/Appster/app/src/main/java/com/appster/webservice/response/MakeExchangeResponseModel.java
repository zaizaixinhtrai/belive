package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 8/23/2016.
 */
public class MakeExchangeResponseModel {
    @SerializedName("TotalBean") @Expose
    private int mTotalBean;
    @SerializedName("TotalGold") @Expose
    private int mTotalGold;

    public int getTotalBean() {
        return mTotalBean;
    }

    public void setTotalBean(int TotalBean) {
        this.mTotalBean = TotalBean;
    }

    public int getTotalGold() {
        return mTotalGold;
    }

    public void setTotalGold(int TotalGold) {
        this.mTotalGold = TotalGold;
    }
}
