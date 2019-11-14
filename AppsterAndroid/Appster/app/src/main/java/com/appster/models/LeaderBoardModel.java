package com.appster.models;

/**
 * Created by User on 10/5/2015.
 */
public class LeaderBoardModel {

    private String UserId;
    private String UserName;
    private String DisplayName;
    private String UserImage;
    private String Gender;
    private int FollowerCount;
    private int GiftReceivedCount;
    private int GiftSentCount;
    private long TotalBean;
    private long TotalGold;
    private int FollowingCount;

    public long getTotalBean() {
        return TotalBean;
    }

    public void setTotalBean(long totalBean) {
        TotalBean = totalBean;
    }

    public long getTotalGold() {
        return TotalGold;
    }

    public void setTotalGold(long totalGold) {
        TotalGold = totalGold;
    }

    public int getFollowingCount() {
        return FollowingCount;
    }

    public void setFollowingCount(int followingCount) {
        FollowingCount = followingCount;
    }

    public int getGift_sent_count() {
        return GiftSentCount;
    }

    public void setGift_sent_count(int gift_sent_count) {
        this.GiftSentCount = gift_sent_count;
    }

    public String getId() {
        return UserId;
    }

    public void setId(String id) {
        this.UserId = id;
    }

    public String getUsername() {
        return UserName;
    }

    public void setUsername(String username) {
        this.UserName = username;
    }

    public String getDisplay_name() {
        return DisplayName;
    }

    public void setDisplay_name(String display_name) {
        this.DisplayName = display_name;
    }

    public String getProfilePic() {
        return UserImage;
    }

    public void setProfilePic(String profilePic) {
        UserImage = profilePic;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public int getFollowers_count() {
        return FollowerCount;
    }

    public void setFollowers_count(int followers_count) {
        this.FollowerCount = followers_count;
    }

    public int getGift_received_count() {
        return GiftReceivedCount;
    }

    public void setGift_received_count(int gift_received_count) {
        this.GiftReceivedCount = gift_received_count;
    }
}
