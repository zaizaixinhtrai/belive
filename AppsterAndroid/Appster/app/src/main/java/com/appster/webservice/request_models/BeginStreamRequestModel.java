package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 6/21/2016.
 */
public class BeginStreamRequestModel {
    @SerializedName("Slug")
    private String mSlug;
    @SerializedName("PrivatePassword")
    private String privatePassword ="";
    public BeginStreamRequestModel(String slug){
        this.mSlug = slug;
    }

    public void setPrivatePassword(String privatePassword) {
        this.privatePassword = privatePassword;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }
}
