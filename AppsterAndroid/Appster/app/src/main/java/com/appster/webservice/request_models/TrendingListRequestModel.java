package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/5/2015.
 */
public class TrendingListRequestModel {
    @SerializedName("Type") @Expose
    private int mType;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

}
