package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/29/2016.
 */

public class DeleteStreamRequestModel {
    @SerializedName("Slug") @Expose
    private String mSlug;

    public DeleteStreamRequestModel(String slug) {
        mSlug = slug;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }
}
