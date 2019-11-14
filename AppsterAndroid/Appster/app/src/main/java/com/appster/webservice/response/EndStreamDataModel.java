package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 7/5/2016.
 */
public class EndStreamDataModel {
    @SerializedName("ViewCount") @Expose
    private long mViewCount;
    @SerializedName("GiftCount") @Expose
    private int mGiftCount;
    @SerializedName("LikeCount") @Expose
    private int mLikeCount;
    @SerializedName("Duration") @Expose
    private long mDuration;
    @SerializedName("TotalGold") @Expose
    private long mTotalGold;

    public long getTotalGold() {
        return mTotalGold;
    }

    public void setTotalGold(long totalGold) {
        mTotalGold = totalGold;
    }

    public long getViewCount() {
        return mViewCount;
    }

    public void setViewCount(long viewCount) {
        mViewCount = viewCount;
    }

    public int getGiftCount() {
        return mGiftCount;
    }

    public void setGiftCount(int giftCount) {
        mGiftCount = giftCount;
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }
}
