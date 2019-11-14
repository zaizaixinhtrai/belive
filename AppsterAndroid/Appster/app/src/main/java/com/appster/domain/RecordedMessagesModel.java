package com.appster.domain;

import androidx.annotation.IntDef;

import com.appster.models.StreamTitleSticker;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RecordedMessagesModel {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LIKE = 1;
    public static final int TYPE_GIFT = 2;
    public static final int TYPE_JOIN = 3;
    public static final int TYPE_FOLLOW = 4;
    public static final int TYPE_SHARE = 5;
    public static final int TYPE_STREAM_TITLE_STICKER = 14;
    public static final int TYPE_FOLLOW_HOST_SUGGESTION = 77;
    public static final int TYPE_LIVE_COMMERCE_ANNOUNCEMENT = 15;


    @IntDef({TYPE_MESSAGE, TYPE_LIKE, TYPE_GIFT, TYPE_JOIN, TYPE_STREAM_TITLE_STICKER, TYPE_FOLLOW, TYPE_SHARE, TYPE_FOLLOW_HOST_SUGGESTION,
            TYPE_LIVE_COMMERCE_ANNOUNCEMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionType {
    }

    @SerializedName("UserName")
    @Expose
    private String UserName;
    @SerializedName("DisplayName")
    @Expose
    private String DisplayName;
    @SerializedName("ActionType")
    @Expose
    @ActionType
    private int ActionType;
    @SerializedName("Message")
    @Expose
    private String Message;
    @SerializedName("RecordedTime")
    @Expose
    private float RecordedTime;

    @SerializedName("profilePic")
    @Expose
    private String profilePic;


    @SerializedName("GiftImage")
    @Expose
    private String GiftImage;

    @SerializedName("ProfileColor")
    @Expose
    private String ProfileColor;

    @SerializedName("GiftComboQuantity")
    private int mGiftComboQuantity;

    @SerializedName("textSticker")
    public StreamTitleSticker streamTitleSticker;

    @SerializedName("GiftId")
    private String mGiftId;

    @SerializedName("GiftName")
    private String mGiftName = "";

    @SerializedName("Rank")
    public int rank = -1;

    public String getProfileColor() {
        return ProfileColor;
    }

    public void setProfileColor(String profileColor) {
        ProfileColor = profileColor;
    }

    public String getUserName() {
        return UserName;
    }

    @ActionType
    public int getActionType() {
        return ActionType;
    }

    public int getRecordedTime() {
        return (int) RecordedTime;
    }

    public void setActionType(@ActionType int ActionType) {
        this.ActionType = ActionType;
    }

    public String getMessage() {
        return Message;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public void setRecordedTime(int RecordedTime) {
        this.RecordedTime = RecordedTime;
    }

    @Override
    public String toString() {
        return UserName + "-" + Message + "-" + ActionType + "-" + RecordedTime;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }


    public String getGiftImage() {
        return GiftImage;
    }

    public void setGiftImage(String giftImage) {
        GiftImage = giftImage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String userImage) {
        this.profilePic = userImage;
    }

    public int getGiftComboQuantity() {
        return mGiftComboQuantity;
    }

    public void setGiftComboQuantity(int giftComboQuantity) {
        mGiftComboQuantity = giftComboQuantity;
    }

    public void setStreamTitleSticker(StreamTitleSticker streamTitleSticker) {
        this.streamTitleSticker = streamTitleSticker;
        ActionType = TYPE_STREAM_TITLE_STICKER;
    }

    public String getGiftId() {
        return mGiftId;
    }

    public void setGiftId(String giftId) {
        this.mGiftId = giftId;
    }

    private String getGiftName() {
        return mGiftName;
    }

    public void setGiftName(String giftName) {
        mGiftName = giftName;
    }
}