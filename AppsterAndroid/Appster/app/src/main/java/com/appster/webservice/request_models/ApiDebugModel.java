package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gaku on 5/16/17.
 */

public class ApiDebugModel {

    @SerializedName("data")
    @Expose
    private String data;

    public ApiDebugModel(String data) {
        this.data = data;
    }

}
