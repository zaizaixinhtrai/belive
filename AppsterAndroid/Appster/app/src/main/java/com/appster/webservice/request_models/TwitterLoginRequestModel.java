package com.appster.webservice.request_models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/7/2016.
 */

public class TwitterLoginRequestModel extends BaseLoginRequestModel {

    @SerializedName("TwitterId")
    private String TwitterId;

    public String getTwitterId() {
        return TwitterId;
    }

    public void setTwitterId(String twitterId) {
        TwitterId = twitterId;
    }
}
