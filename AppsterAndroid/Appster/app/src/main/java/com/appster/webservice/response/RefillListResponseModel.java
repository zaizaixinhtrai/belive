package com.appster.webservice.response;

import com.appster.refill.RefillListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 10/5/2015.
 */
public class RefillListResponseModel {

    @SerializedName("TotalBean") @Expose
    private long mTotalBean;
    @SerializedName("TotalGold") @Expose
    private long mTotalGold;
    @SerializedName("TopUps") @Expose
    private List<RefillListItem> mTopUps;

    public List<RefillListItem> getTopUpList() {
        return mTopUps;
    }

    public void setTopUpList(List<RefillListItem> topUpList) {
        mTopUps = topUpList;
    }


    public long getTotal_bean() {
        return mTotalBean;
    }

    public void setTotal_bean(long total_bean) {
        this.mTotalBean = total_bean;
    }

    public long getTotal_gold() {
        return mTotalGold;
    }

    public void setTotal_gold(long total_gold) {
        this.mTotalGold = total_gold;
    }

}
