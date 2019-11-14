package com.appster.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 8/30/2016.
 */
public class TopFanModel implements Parcelable {

    private String UserId;
    private String UserName;
    private String DisplayName;
    private String UserImage;
    private String Gender;
    private int GiftReceivedCount;
    private int GiftSentCount;
    private long TotalBean;
    private long TotalGold;
    private int IsFollow;
    private int TotalGoldSend;
    private String WebProfileUrl;

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

    public int getGiftReceivedCount() {
        return GiftReceivedCount;
    }

    public void setGiftReceivedCount(int GiftReceivedCount) {
        this.GiftReceivedCount = GiftReceivedCount;
    }

    public int getGiftSentCount() {
        return GiftSentCount;
    }

    public void setGiftSentCount(int GiftSentCount) {
        this.GiftSentCount = GiftSentCount;
    }

    public long getTotalBean() {
        return TotalBean;
    }

    public void setTotalBean(long TotalBean) {
        this.TotalBean = TotalBean;
    }

    public long getTotalGold() {
        return TotalGold;
    }

    public void setTotalGold(long TotalGold) {
        this.TotalGold = TotalGold;
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(int IsFollow) {
        this.IsFollow = IsFollow;
    }

    public int getTotalGoldSend() {
        return TotalGoldSend;
    }

    public void setTotalGoldSend(int TotalGoldSend) {
        this.TotalGoldSend = TotalGoldSend;
    }

    public String getWebProfileUrl() {
        return WebProfileUrl;
    }

    public void setWebProfileUrl(String WebProfileUrl) {
        this.WebProfileUrl = WebProfileUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.DisplayName);
        dest.writeString(this.UserImage);
        dest.writeString(this.Gender);
        dest.writeInt(this.GiftReceivedCount);
        dest.writeInt(this.GiftSentCount);
        dest.writeLong(this.TotalBean);
        dest.writeLong(this.TotalGold);
        dest.writeInt(this.IsFollow);
        dest.writeInt(this.TotalGoldSend);
        dest.writeString(this.WebProfileUrl);
    }

    public TopFanModel() {
    }

    protected TopFanModel(Parcel in) {
        this.UserId = in.readString();
        this.UserName = in.readString();
        this.DisplayName = in.readString();
        this.UserImage = in.readString();
        this.Gender = in.readString();
        this.GiftReceivedCount = in.readInt();
        this.GiftSentCount = in.readInt();
        this.TotalBean = in.readLong();
        this.TotalGold = in.readLong();
        this.IsFollow = in.readInt();
        this.TotalGoldSend = in.readInt();
        this.WebProfileUrl = in.readString();
    }

    public static final Parcelable.Creator<TopFanModel> CREATOR = new Parcelable.Creator<TopFanModel>() {
        @Override
        public TopFanModel createFromParcel(Parcel source) {
            return new TopFanModel(source);
        }

        @Override
        public TopFanModel[] newArray(int size) {
            return new TopFanModel[size];
        }
    };
}
