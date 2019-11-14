package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 8/22/2017.
 */

public class SearchPostModel {

    @SerializedName("PostId")
    @Expose
    public int postId;
    @SerializedName("Title")
    @Expose
    public String title;
    @SerializedName("MediaType")
    @Expose
    public int mediaType;
    @SerializedName("MediaImage")
    @Expose
    public String mediaImage;
    @SerializedName("MediaImageThumbnail")
    @Expose
    public String mediaImageThumbnail;
    @SerializedName("MediaVideo")
    @Expose
    public String mediaVideo;
    @SerializedName("Address")
    @Expose
    public String address;
    @SerializedName("WebPostUrl")
    @Expose
    public String webPostUrl;
}
