package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 7/8/2016.
 */
public class TagListLiveStreamModel {

    @SerializedName("TagId")
    @Expose
    private int TagId;
    @SerializedName("TagName")
    @Expose
    private String TagName;
    @SerializedName("TagImage")
    @Expose
    private String TagImage;

    public int getTagId() {
        return TagId;
    }

    public void setTagId(int TagId) {
        this.TagId = TagId;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String TagName) {
        this.TagName = TagName;
    }

    public String getTagImage() {
        return TagImage;
    }

    public void setTagImage(String TagImage) {
        this.TagImage = TagImage;
    }
}
