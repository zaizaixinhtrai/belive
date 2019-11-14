package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/3/2015.
 */
public class GiftReceiverModel {

    @SerializedName("GiftCount")
    @Expose
    private int GiftCount;

    @SerializedName("GiftId")
    @Expose
    private String GiftId;

    @SerializedName("GiftImage")
    @Expose
    private String GiftImage;

    @SerializedName("GiftName")
    @Expose
    private String GiftName;

    public int getGift_count() {
        return GiftCount;
    }

    public void setGift_count(int gift_count) {
        this.GiftCount = gift_count;
    }

    public String getGift_id() {
        return GiftId;
    }

    public void setGift_id(String gift_id) {
        this.GiftId = gift_id;
    }

    public String getGift_image() {
        return GiftImage;
    }

    public void setGift_image(String gift_image) {
        this.GiftImage = gift_image;
    }

    public String getGift_name() {
        return GiftName;
    }

    public void setGift_name(String gift_name) {
        this.GiftName = gift_name;
    }
}
