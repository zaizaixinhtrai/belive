package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneVerificationRequest {
    @SerializedName("CountryCode") @Expose
    public String countryCode;
    @SerializedName("Phone") @Expose
    public String phoneNumber;

    public PhoneVerificationRequest(String countryCode, String phoneNumber) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
    }
}
