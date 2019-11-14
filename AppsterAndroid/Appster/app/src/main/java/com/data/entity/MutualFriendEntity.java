package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 12/27/17.
 */

public class MutualFriendEntity  {
    @SerializedName("UserName")
    public String userName;

    @SerializedName("UserImage")
    public String userImage;

    @SerializedName("UserId")
    public String userId;

    @SerializedName("DisplayName")
    public String displayName;

    @SerializedName("WebProfileUrl")
    public String webProfileUrl;

    @SerializedName("Gender")
    public String gender;

    @SerializedName("IsFollow")
    public int isFollow;

    @SerializedName("PhoneNumber")
    public String phoneNumber="";

    @SerializedName("NormalizedPhone")
    public String normalizedPhone="";
}
