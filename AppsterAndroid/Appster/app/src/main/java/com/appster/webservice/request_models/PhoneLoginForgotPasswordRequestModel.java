package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneLoginForgotPasswordRequestModel {
    @SerializedName("CountryCode") @Expose
    public String countryCode;
    @SerializedName("Phone") @Expose
    public String phoneNumber;

    public PhoneLoginForgotPasswordRequestModel(String countryCode, String phoneNumber) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
    }
}
