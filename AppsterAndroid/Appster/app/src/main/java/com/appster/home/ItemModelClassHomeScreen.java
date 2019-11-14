package com.appster.home;

public class ItemModelClassHomeScreen {

    private String UserId;
    private String UserName;
    private String DisplayName;
    private String UserImage;
    private String Gender;
    private double Distance;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String UserImage) {
        this.UserImage = UserImage;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double Distance) {
        this.Distance = Distance;
    }
}
