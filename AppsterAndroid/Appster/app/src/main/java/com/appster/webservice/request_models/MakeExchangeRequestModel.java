package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 8/23/2016.
 */
public class MakeExchangeRequestModel {
    @SerializedName("ExchangeId") @Expose
    private int mExchangeId;

    public MakeExchangeRequestModel(int exchangeId) {
        mExchangeId = exchangeId;
    }

    public int getExchangeId() {
        return mExchangeId;
    }

    public void setExchangeId(int exchangeId) {
        mExchangeId = exchangeId;
    }
}
