package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/23/17.
 */

public class VotingSetting {
    @SerializedName("LuckyWheelStatus")
    @Expose
    public boolean luckyWheelStatus;
}
