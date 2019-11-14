package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchUserEntity{
    @SerializedName("UserId")
    internal val userId: String?=null

    @SerializedName("UserName")
    internal val username: String?=null

    @SerializedName("DisplayName")
    internal val displayName: String?=null

    @SerializedName("UserImage")
    internal val userProfilePic: String?=null

    @SerializedName("Gender")
    internal val gender: String?=null

    @SerializedName("IsFollow")
    internal val isFollow: Int = 0

    internal val typeModel :Int =0;

}