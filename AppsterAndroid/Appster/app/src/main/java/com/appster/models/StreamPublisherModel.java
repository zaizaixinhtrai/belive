package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pack.utility.StringUtil;

/**
 * Created by sonnguyen on 11/27/16.
 */

public class StreamPublisherModel {

    private String UserId;
    private String UserName;
    private String DisplayName;
    private String UserImage;
    private String Gender;
    private String Handle;
    private int Score;
    private int IsFollow;
    private int FollowerCount;
    private long TotalGold;
    private int VotingScores;
    private long TotalGoldFans;
    @SerializedName("IsSeller") @Expose
    private boolean mIsSeller;

    @SerializedName("Point")
    @Expose
    public int point;

    public long getTotalGoldFans() {
        return TotalGoldFans;
    }

    public void setTotalGoldFans(long totalGoldFans) {
        TotalGoldFans = totalGoldFans;
    }

    public long getTotalGold() {
        return TotalGold;
    }

    public void setTotalGold(long totalGold) {
        TotalGold = totalGold;
    }

    public int getFollowerCount() {
        return FollowerCount;
    }

    public void setFollowerCount(int followerCount) {
        FollowerCount = followerCount;
    }

    public boolean isFollow() {
        return IsFollow == 1;
    }

    public void setIsFollow(int isFollow) {
        IsFollow = isFollow;
    }
    public int getIsFollow() {
        return IsFollow;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String UserImage) {
        this.UserImage = UserImage;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getHandle() {
        return Handle;
    }

    public void setHandle(String Handle) {
        this.Handle = Handle;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int Score) {
        this.Score = Score;
    }

    public boolean isSeller() {
        return mIsSeller;
    }

    public void setSeller(boolean seller) {
        mIsSeller = seller;
    }


    public String getNameShowInClient(){
        if(StringUtil.isNullOrEmptyString(getDisplayName())){
            return getUserName();
        }else{
            return getDisplayName();
        }
    }

    public StreamPublisherModel(UserModel modelNew){
        setIsFollow(modelNew.getIsFollow());
        setDisplayName(modelNew.getDisplayName());
        setUserName(modelNew.getUserName());
        setUserId(modelNew.getUserId());
        setUserImage(modelNew.getUserImage());
    }

    public int getVotingScores() {
        return VotingScores;
    }
}
