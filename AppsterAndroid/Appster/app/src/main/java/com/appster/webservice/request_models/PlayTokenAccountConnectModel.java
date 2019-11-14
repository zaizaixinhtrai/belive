package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gaku on 5/16/17.
 */

public class PlayTokenAccountConnectModel {

    @SerializedName("playtokenId")
    @Expose
    private String playtokenId;

    public PlayTokenAccountConnectModel(String playtokenId) {
        this.playtokenId = playtokenId;
    }

}
