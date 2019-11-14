package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 8/8/17.
 */

public class BotFollowRequestModel {
    @SerializedName("BotId") @Expose
    private int botId;

    public BotFollowRequestModel(int botId) {
        this.botId = botId;
    }
}
