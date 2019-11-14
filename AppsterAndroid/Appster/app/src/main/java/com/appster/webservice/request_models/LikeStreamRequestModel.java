package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 7/1/2016.
 */
public class LikeStreamRequestModel {
    @SerializedName("Slug") @Expose
    private String mSlug;
    @SerializedName("Like") @Expose
    private Boolean mLike;


    public LikeStreamRequestModel(String slug,Boolean isLike){
        setLike(isLike);
        setSlug(slug);
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public Boolean getLike() {
        return mLike;
    }

    public void setLike(Boolean like) {
        mLike = like;
    }
}
