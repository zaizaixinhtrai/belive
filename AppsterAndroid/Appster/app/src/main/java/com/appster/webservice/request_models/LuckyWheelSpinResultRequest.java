package com.appster.webservice.request_models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thanhbc on 2/22/17.
 */

public class LuckyWheelSpinResultRequest  {
    @SerializedName("UserJoined")
    @Expose
    private String mUserJoined;

    public LuckyWheelSpinResultRequest(List<String> userJoined) {
        this.mUserJoined=TextUtils.join(";",userJoined);
    }
}
