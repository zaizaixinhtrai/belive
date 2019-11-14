package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/11/2015.
 */
public class UpdateDeviceTokenRequestModel {
    @SerializedName("DeviceToken") @Expose
    private String mDeviceToken;
    @SerializedName("DeviceType") @Expose
    private int mDeviceType;
    @SerializedName("DeviceUdid") @Expose
    private String mDeviceUdid;

    public int getDevice_type() {
        return mDeviceType;
    }

    public void setDevice_type(int device_type) {
        this.mDeviceType = device_type;
    }

    public String getDevice_token() {
        return mDeviceToken;
    }

    public void setDevice_token(String device_token) {
        this.mDeviceToken = device_token;
    }

    public String getDevice_udid() {
        return mDeviceUdid;
    }

    public void setDevice_udid(String device_udid) {
        this.mDeviceUdid = device_udid;
    }
}
