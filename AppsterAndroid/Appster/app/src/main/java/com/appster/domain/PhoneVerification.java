package com.appster.domain;

import com.google.gson.annotations.SerializedName;

public class PhoneVerification{

	@SerializedName("IsExists")
	public boolean isExists;

	@SerializedName("OtpToken")
	public String otpToken;

	@Override
 	public String toString(){
		return 
			"PhoneVerification{" + 
			"isExists = '" + isExists + '\'' + 
			",otpToken = '" + otpToken + '\'' + 
			"}";
		}
}