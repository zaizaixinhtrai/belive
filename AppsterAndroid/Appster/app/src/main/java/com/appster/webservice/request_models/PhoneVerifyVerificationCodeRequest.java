package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneVerifyVerificationCodeRequest {
    @SerializedName("OtpToken") @Expose
    public String otp;

    @SerializedName("Otp") @Expose
    public String verificationCode;

    public PhoneVerifyVerificationCodeRequest(String otp, String verificationCode) {
        this.otp = otp;
        this.verificationCode = verificationCode;
    }
}
