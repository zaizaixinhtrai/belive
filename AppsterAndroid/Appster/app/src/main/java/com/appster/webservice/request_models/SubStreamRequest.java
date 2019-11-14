package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 9/8/17.
 */

public class SubStreamRequest {
    @SerializedName("UserId")
    @Expose
    private int userId;

    public SubStreamRequest(int userId) {
        this.userId = userId;
    }
}
