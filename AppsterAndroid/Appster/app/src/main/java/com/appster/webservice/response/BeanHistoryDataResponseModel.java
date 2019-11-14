package com.appster.webservice.response;

import com.appster.models.PocketHistoryModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 7/18/2016.
 */
public class BeanHistoryDataResponseModel extends BaseDataPagingResponseModel<PocketHistoryModel> {
    @SerializedName("TotalBean") @Expose
    private int mTotalBean;
    @SerializedName("TotalGold") @Expose
    private int mTotalGold;

    public int getTotalBean() {
        return mTotalBean;
    }

    public void setTotalBean(int totalBean) {
        mTotalBean = totalBean;
    }

    public int getTotalGold() {
        return mTotalGold;
    }

    public void setTotalGold(int totalGold) {
        mTotalGold = totalGold;
    }
}
