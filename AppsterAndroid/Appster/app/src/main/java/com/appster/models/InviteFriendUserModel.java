package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/16/2015.
 */
public class InviteFriendUserModel {

    @SerializedName("UserId")
    @Expose
    private String UserId;
    @SerializedName("DisplayName")
    @Expose
    private String DisplayName;
    @SerializedName("UserImage")
    @Expose
    private String UserImage;
    @SerializedName("Gender")
    @Expose
    private String Gender;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("IsFollow")
    @Expose
    private int IsFollow;
    @SerializedName("UserName")
    @Expose
    private String UserName;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getIs_follow() {
        return IsFollow;
    }

    public void setIs_follow(int is_follow) {
        this.IsFollow = is_follow;
    }
}
