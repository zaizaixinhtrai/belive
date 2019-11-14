package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/22/17.
 */

public class SpinRequest {
    @SerializedName("LevelId")
    @Expose
    private int mLevelId;

    @SerializedName("Slug")
    @Expose
    private String mSlug;


    public SpinRequest(int levelId, String slug) {
        this.mLevelId = levelId;
        this.mSlug = slug;
    }
}
