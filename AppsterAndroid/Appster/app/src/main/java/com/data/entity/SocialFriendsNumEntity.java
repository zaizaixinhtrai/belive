package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 12/30/17.
 */

public class SocialFriendsNumEntity {
    @SerializedName("NumberFaceBookFriend")
    public int facebookFriends;

    @SerializedName("NumberContactFriend")
    public int contactFriends;

    @SerializedName("NumberTwitterFriend")
    public int twitterFriends;

    @SerializedName("NumberInstagramFriend")
    public int instagramFriends;

}
