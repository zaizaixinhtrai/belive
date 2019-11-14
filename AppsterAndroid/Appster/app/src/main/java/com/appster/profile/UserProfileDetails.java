package com.appster.profile;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;

/**
 * Created by Ngoc on 8/31/2015.
 */
public class UserProfileDetails extends BaseBundle {
    private String id = "";
    private String username = "";
    private String display_name = "";
    private int is_follow = 0;
    private int followers_count;
    private int following_count;
    private int gift_sent_count;
    private int gift_received_count;
    private int follow_count;
    private String is_login = "";
    private String last_check_in_address = "";
    private String ProfilePic = "";
    private String dob = "";
    private String gender;
    private boolean can_change_password;

    public static Creator<UserProfileDetails> getCREATOR() {
        return CREATOR;
    }


    public UserProfileDetails() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(display_name);
        dest.writeInt(is_follow);
        dest.writeInt(followers_count);
        dest.writeInt(following_count);
        dest.writeInt(gift_sent_count);
        dest.writeInt(gift_received_count);
        dest.writeInt(follow_count);
        dest.writeString(is_login);
        dest.writeString(last_check_in_address);
        dest.writeString(ProfilePic);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(can_change_password ? "true" : "false");
    }

    public UserProfileDetails(Parcel in) {
        super(in);
        this.id = in.readString();
        this.username = in.readString();
        this.display_name = in.readString();
        this.is_follow = in.readInt();
        this.followers_count = in.readInt();
        this.following_count = in.readInt();
        this.gift_sent_count = in.readInt();
        this.gift_received_count = in.readInt();
        this.follow_count = in.readInt();
        this.is_login = in.readString();
        this.last_check_in_address = in.readString();
        ProfilePic = in.readString();
        this.dob = in.readString();
        this.gender = in.readString();
        this.can_change_password = (in.readString().equals("false")) ? false : true;
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     * <p/>
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Creator<UserProfileDetails> CREATOR = new Creator<UserProfileDetails>() {

        public UserProfileDetails createFromParcel(Parcel in) {
            return new UserProfileDetails(in);
        }

        public UserProfileDetails[] newArray(int size) {
            return new UserProfileDetails[size];
        }

    };

    public boolean isCan_change_password() {
        return can_change_password;
    }

    public void setCan_change_password(boolean can_change_password) {
        this.can_change_password = can_change_password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public int getGift_sent_count() {
        return gift_sent_count;
    }

    public void setGift_sent_count(int gift_sent_count) {
        this.gift_sent_count = gift_sent_count;
    }

    public int getGift_received_count() {
        return gift_received_count;
    }

    public void setGift_received_count(int gift_received_count) {
        this.gift_received_count = gift_received_count;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public String getIs_login() {
        return is_login;
    }

    public void setIs_login(String is_login) {
        this.is_login = is_login;
    }

    public String getLast_check_in_address() {
        return last_check_in_address;
    }

    public void setLast_check_in_address(String last_check_in_address) {
        this.last_check_in_address = last_check_in_address;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
