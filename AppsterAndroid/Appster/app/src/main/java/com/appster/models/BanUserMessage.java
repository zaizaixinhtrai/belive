package com.appster.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 4/18/17.
 */

public class BanUserMessage {

    @SerializedName("UserName")
    public String userName;
    @SerializedName("UserId")
    public Object userId;
    @SerializedName("Message")
    public String message;
    @SerializedName("DisplayName")
    public Object displayName;
    @SerializedName("ProfilePic")
    public String profilePic;
    @SerializedName("MessageType")
    public String messageType;
    @SerializedName("Slug")
    public String slug;

    @Override
    public String toString() {
        return "BanUserMessage{" +
                "userName='" + userName + '\'' +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", displayName=" + displayName +
                ", profilePic='" + profilePic + '\'' +
                ", messageType='" + messageType + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
