package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneVerificationRequestVerificationCode {
    @SerializedName("OtpToken") @Expose
    public String otp;

    public PhoneVerificationRequestVerificationCode(String otp) {
        this.otp = otp;
    }
}
