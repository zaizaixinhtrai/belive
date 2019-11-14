package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/7/2016.
 */
public class ChatBotUserRequestModel {

    @SerializedName("Slug")
    @Expose
    private String mSlug;
    @SerializedName("AtTime")
    @Expose
    private int mAtTime;

    @SerializedName("PageIndex")
    @Expose
    private int mPageIndex;

    public ChatBotUserRequestModel(String slug, int atTime, int pageIndex){
        this.mSlug =slug;
        this.mAtTime = atTime;
        this.mPageIndex = pageIndex;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        this.mSlug = slug;
    }


    public int getAtTime() {
        return mAtTime;
    }

    public void setAtTime(int atTime) {
        this.mAtTime = atTime;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int page) {
        this.mPageIndex = page;
    }
}
