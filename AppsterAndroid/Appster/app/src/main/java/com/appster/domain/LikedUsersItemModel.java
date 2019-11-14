package com.appster.domain;


import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/27/2016.
 */
public class LikedUsersItemModel{
    @SerializedName("UserId")private String UserId;
    @SerializedName("DisplayName")private String DisplayName;
    @SerializedName("UserImage")private String UserImage;
    @SerializedName("Gender")private String Gender;
    @SerializedName("IsFollow")private int IsFollow;
    @SerializedName("UserName")private String UserName;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getIs_follow() {
        return IsFollow;
    }

    public void setIs_follow(int is_follow) {
        this.IsFollow = is_follow;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
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
        Gender = gender;
    }
}
