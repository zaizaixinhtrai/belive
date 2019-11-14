package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 01/11/2016.
 */

public class GoogleLoginRequestModel extends BaseLoginRequestModel {
    @SerializedName("GoogleId") @Expose
    private String GoogleId;

    public String getGoogleId() {
        return GoogleId;
    }

    public void setGoogleId(String googleId) {
        GoogleId = googleId;
    }
}
