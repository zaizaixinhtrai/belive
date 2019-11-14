package com.appster.webservice.request_models;

import com.google.gson.annotations.SerializedName;

public class PhoneLoginResetPasswordRequest {

	@SerializedName("OtpToken")
	public String otpToken;

	@SerializedName("Password")
	public String password;
}