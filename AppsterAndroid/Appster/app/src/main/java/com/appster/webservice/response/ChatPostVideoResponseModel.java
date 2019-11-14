package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/16/2015.
 */
public class ChatPostVideoResponseModel extends BaseDataResponseModel {
    @SerializedName("ImageUrl") @Expose
    private String mImageUrl;
    @SerializedName("VideoUrl") @Expose
    private String mVideoUrl;

    public String getVideo_url() {
        return mVideoUrl;
    }

    public void setVideo_url(String video_url) {
        this.mVideoUrl = video_url;
    }


    public String getImage_url() {
        return mImageUrl;
    }

    public void setImage_url(String image_url) {
        this.mImageUrl = image_url;
    }
}
