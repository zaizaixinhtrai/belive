package com.appster.models;

import com.pack.utility.StringUtil;

/**
 * Created by User on 7/28/2016.
 */
public class SettingResponse {

    private int UserId;
    private String UserName;
    private String DisplayName;
    private String Email;
    private String UserImage;
    private String Gender;
    private String DoB;
    private String Nationality;
    private int EmailVerified;
    private int Status;
    private int RefId;
    private int ReferralId;
    private String About;
    private String WebProfileUrl;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getDisplayName() {
        return StringUtil.decodeString(DisplayName);
    }

    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
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

    public String getDoB() {
        return DoB;
    }

    public void setDoB(String DoB) {
        this.DoB = DoB;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String Nationality) {
        this.Nationality = Nationality;
    }

    public int getEmailVerified() {
        return EmailVerified;
    }

    public void setEmailVerified(int EmailVerified) {
        this.EmailVerified = EmailVerified;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public int getRefId() {
        return RefId;
    }

    public void setRefId(int RefId) {
        this.RefId = RefId;
    }

    public int getReferralId() {
        return ReferralId;
    }

    public void setReferralId(int ReferralId) {
        this.ReferralId = ReferralId;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String About) {
        this.About = About;
    }

    public String getWebProfileUrl() {
        return WebProfileUrl;
    }

    public void setWebProfileUrl(String WebProfileUrl) {
        this.WebProfileUrl = WebProfileUrl;
    }
}
