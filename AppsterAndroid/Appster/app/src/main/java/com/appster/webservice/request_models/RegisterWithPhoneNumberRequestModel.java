package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by linh on 25/10/2017.
 */

public class RegisterWithPhoneNumberRequestModel extends BaseRegisterRequestModel {

    @SerializedName("CountryCode") @Expose
    public String countryCode;
    @SerializedName("Phone") @Expose
    public String phoneNumber;
    @SerializedName("Password") @Expose
    public String passWord;

    public RegisterWithPhoneNumberRequestModel(String username, String displayname, int device_type, String device_udid, String device_token, double latitude, double longitude, String address, String email, File profile, String ref_id, String Gender, String deviceName, String OSVersion, String version, String countryCode, String phoneNumber, String passWord) {
        super(username, displayname, device_type, device_udid, device_token, latitude, longitude, address, email, profile, ref_id, Gender, deviceName, OSVersion, version);
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.passWord = passWord;
        handleAddPartData();
    }

    @Override
    protected void handleAddPartData() {
        super.handleAddPartData();
        addPartNotEmptyString("CountryCode", countryCode);
        addPartNotEmptyString("Phone", phoneNumber);
        addPartNotEmptyString("Password", passWord);
    }
}
