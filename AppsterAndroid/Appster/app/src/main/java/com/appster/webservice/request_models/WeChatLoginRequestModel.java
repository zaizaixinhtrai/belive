package com.appster.webservice.request_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 13/12/2016.
 */

public class WeChatLoginRequestModel extends BaseLoginRequestModel {
    @SerializedName("WeChatId")
    private String mWeChatId;

    public String getWeChatId() {
        return mWeChatId;
    }

    public void setWeChatId(String weChatId) {
        mWeChatId = weChatId;
    }
}
