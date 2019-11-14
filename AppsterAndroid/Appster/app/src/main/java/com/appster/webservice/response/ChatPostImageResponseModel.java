package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/15/2015.
 */

public class ChatPostImageResponseModel {
    @SerializedName("ImageUrl") @Expose
    private String mImageUrl;
    @SerializedName("ThumbnailUrl") @Expose
    private String mThumbnailUrl;

    public String getImage_url() {
        return mImageUrl;
    }

    public void setImage_url(String image_url) {
        this.mImageUrl = image_url;
    }

    public String getThumbnail_url() {
        return mThumbnailUrl;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.mThumbnailUrl = thumbnail_url;
    }
}
