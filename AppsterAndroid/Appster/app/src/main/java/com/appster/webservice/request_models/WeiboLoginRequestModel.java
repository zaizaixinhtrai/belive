package com.appster.webservice.request_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 13/12/2016.
 */

public class WeiboLoginRequestModel extends BaseLoginRequestModel {
    @SerializedName("WeiboId")
    private String mWeiboId;

    public String getWeiboId() {
        return mWeiboId;
    }

    public void setWeiboId(String weiboId) {
        mWeiboId = weiboId;
    }
}
