package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 3/9/2018.
 */

public class TriviaRankingListEntity {

    @SerializedName("UserId")
    public int userId;

    @SerializedName("UserAvatar")
    public String userAvatar;

    @SerializedName("DisplayName")
    public String displayName;

    @SerializedName("Username")
    public String userName;

    @SerializedName("Prize")
    public double prize;

    @SerializedName("PrizeString")
    public String prizeString;

    public int orderIndex;
}
