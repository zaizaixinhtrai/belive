package com.appster.webservice.response;

import androidx.annotation.IntDef;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ThanhBan on 9/7/2016.
 */
public class ChatBotUserModel {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LIKE = 1;
    public static final int TYPE_GIFT = 2;
    public static final int TYPE_NONE = 3;
    public static final int TYPE_FOLLOW = 4;

    @IntDef({TYPE_MESSAGE, TYPE_LIKE, TYPE_GIFT, TYPE_NONE, TYPE_FOLLOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatBotActionType {
    }

    @SerializedName("UserId")
    @Expose
    public int userId;
    @SerializedName("UserName")
    @Expose
    public String userName;
    @SerializedName("DisplayName")
    @Expose
    public String displayName;
    @SerializedName("UserImage")
    @Expose
    public String userImage;
    @SerializedName("Message")
    @Expose
    public String message;
    @SerializedName("AtTime")
    @Expose
    public int atTime;


    @SerializedName("DelayTimeLeave")
    @Expose
    public int leaveTime;

    @SerializedName("DelayTime")
    @Expose
    public int delayTime;

    @SerializedName("ActionType")
    @Expose
    public int actionType;

    @SerializedName("Gift")
    @Expose
    public Gift gift;

    @SerializedName("CostGold")
    @Expose
    public int costGold;

    @SerializedName("CostBean")
    @Expose
    public int costBean;

    @ChatBotActionType
    public int getActionType() {
        return actionType;
    }

    public class Gift {

        @SerializedName("GiftId")
        @Expose
        public int giftId;
        @SerializedName("GiftName")
        @Expose
        public String giftName;
        @SerializedName("Image")
        @Expose
        public String image;

    }

    @Override
    public String toString() {
        return "ChatBotUserModel{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userImage='" + userImage + '\'' +
                ", message='" + message + '\'' +
                ", atTime=" + atTime +
                ", leaveTime=" + leaveTime +
                ", delayTime=" + delayTime +
                ", actionType=" + actionType +
                ", gift=" + gift +
                ", costGold=" + costGold +
                ", costBean=" + costBean +
                '}';
    }
}

