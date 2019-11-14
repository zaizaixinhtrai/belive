package com.appster.webservice.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 8/16/2016.
 */
public class GetUserImageResponseModel {
    @SerializedName("image")
    private String mImage;
    @SerializedName("displayName")
    private String mDisplayName;

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }
}
