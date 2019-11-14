package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/22/17.
 */

public class LuckyWheelSpinResponseModel {
    @SerializedName("SpinId")
    @Expose
    public int spinId;

    @SerializedName("AwardId")
    @Expose
    public int awardId;
}
