package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Son Nguyen on 6/21/2016.
 */
public class CreateStreamRequestModel {
    @SerializedName("StreamTitle") @Expose
    private String mStreamTitle;
    @SerializedName("IsRecored") @Expose
    private Boolean mIsRecored;
    @SerializedName("TagId") @Expose
    private int mTagId;
    @SerializedName("Latitude") @Expose
    private double mLatitude;
    @SerializedName("Longitude") @Expose
    private double mLongitude;
    @SerializedName("FrameRate") @Expose
    private int mFrameRate;
    @SerializedName("CountryCode") @Expose
    private String mCountryCode;
    @SerializedName("WowzaApplication")
    private String mWowzaApplicationName;

    @SerializedName("IsTrivia")
    public boolean isTrivia;
    public int getTagId() {
        return mTagId;
    }

    public void setTagId(int tagId) {
        mTagId = tagId;
    }

    public String getStreamTitle() {
        return mStreamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        mStreamTitle = streamTitle;
    }

    public Boolean getRecored() {
        return mIsRecored;
    }

    public void setRecored(Boolean recored) {
        mIsRecored = recored;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getWowzaApplicationName() {
        return mWowzaApplicationName;
    }

    public void setWowzaApplicationName(String wowzaApplicationName) {
        this.mWowzaApplicationName = wowzaApplicationName;
    }

    public int getWowzaVideoFrameRate() {
        return mFrameRate;
    }

    public void setWowzaVideoFrameRate(int wowzaVideoFrameRate) {
        this.mFrameRate = wowzaVideoFrameRate;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }
}
