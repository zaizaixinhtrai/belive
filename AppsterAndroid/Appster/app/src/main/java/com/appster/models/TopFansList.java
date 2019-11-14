package com.appster.models;

import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by linh on 29/01/2018.
 */

public class TopFansList {
    @SerializedName("AllTopFans") @Expose
    public BaseDataPagingResponseModel<TopFanModel> topFans;
    @SerializedName("DailyTopFans") @Expose
    public List<DailyTopFanModel> dailyTopFans;
}
