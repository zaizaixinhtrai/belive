package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneLoginForgotPasswordResponse {
    @SerializedName("OtpToken") @Expose
    public String otp;

    public PhoneLoginForgotPasswordResponse(String otp) {
        this.otp = otp;
    }
}
