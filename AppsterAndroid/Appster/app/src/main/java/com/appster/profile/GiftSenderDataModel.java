package com.appster.profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/22/2015.
 */
public class GiftSenderDataModel implements Parcelable{

    @SerializedName("UserId")
    private String UserId;

    @SerializedName("RoleId")
    private String RoleId;

    @SerializedName("DisplayName")
    private String DisplayName;

    @SerializedName("Gender")
    private String Gender;

    @SerializedName("ProfilePic")
    private String ProfilePic;

    @SerializedName("is_follow")
    private int is_follow;

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String roleId) {
        RoleId = roleId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserId);
        dest.writeString(this.RoleId);
        dest.writeString(this.DisplayName);
        dest.writeString(this.Gender);
        dest.writeString(this.ProfilePic);
        dest.writeInt(this.is_follow);
    }

    public GiftSenderDataModel() {
    }

    protected GiftSenderDataModel(Parcel in) {
        this.UserId = in.readString();
        this.RoleId = in.readString();
        this.DisplayName = in.readString();
        this.Gender = in.readString();
        this.ProfilePic = in.readString();
        this.is_follow = in.readInt();
    }

    public static final Creator<GiftSenderDataModel> CREATOR = new Creator<GiftSenderDataModel>() {
        public GiftSenderDataModel createFromParcel(Parcel source) {
            return new GiftSenderDataModel(source);
        }

        public GiftSenderDataModel[] newArray(int size) {
            return new GiftSenderDataModel[size];
        }
    };
}
