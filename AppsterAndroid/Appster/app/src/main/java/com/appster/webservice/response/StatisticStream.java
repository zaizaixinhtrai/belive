package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 6/24/2016.
 */
public class StatisticStream {
    @SerializedName("ViewCount") @Expose
    private int mViewCount;
    @SerializedName("GiftCount") @Expose
    private int mGiftCount;
    @SerializedName("LikeCount") @Expose
    private int mLikeCount;
    @SerializedName("FollowerCount") @Expose
    private int mFollowerCount;
    @SerializedName("Status") @Expose
    private int mStatus;
    @SerializedName("TotalGold") @Expose
    private long mTotalGold;
    @SerializedName("Duration") @Expose
    private long mDuration;
    @SerializedName("VotingScores") @Expose
    private int mVotingScores;

    @SerializedName("StatusMessage") @Expose
    private String mStatusMessage;

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public long getTotalGold() {
        return mTotalGold;
    }

    public void setTotalGold(long totalGold) {
        mTotalGold = totalGold;
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }

    public int getViewCount() {
        return mViewCount;
    }

    public void setViewCount(int viewCount) {
        mViewCount = viewCount;
    }

    public int getGiftCount() {
        return mGiftCount;
    }

    public void setGiftCount(int giftCount) {
        mGiftCount = giftCount;
    }

    public int getFollowerCount() {
        return mFollowerCount;
    }

    public void setFollowerCount(int followerCount) {
        mFollowerCount = followerCount;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getVotingScores() {
        return mVotingScores;
    }

    public String getStatusMessage() {
        return mStatusMessage;
    }
}
