package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sonnguyen on 11/21/16.
 */

public class VideosCountModel {
    @SerializedName("ViewCount") @Expose
    public long mViewCount;
}
