package com.appster.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by User on 9/28/2015.
 */
@Entity
public class FollowItemModel {


    @PrimaryKey @NonNull private String UserId;
    @ColumnInfo(name = "DisplayName")private String DisplayName;
    @ColumnInfo(name = "UserImage")private String UserImage;
    @ColumnInfo(name = "Gender")private String Gender;
    @ColumnInfo(name = "IsFollow")private int IsFollow;
    @ColumnInfo(name = "UserName")private String UserName;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(int is_follow) {
        this.IsFollow = is_follow;
    }

    @NotNull
    public String getUserId() {
        return UserId;
    }

    public void setUserId(@NotNull String userId) {
        UserId = userId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
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
