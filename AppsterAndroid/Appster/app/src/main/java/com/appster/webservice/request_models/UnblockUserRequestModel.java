package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnblockUserRequestModel{

	@SerializedName("UserName")
	@Expose
	private String mUserName;

	@SerializedName("UnBlockUserId")
	@Expose
	private String mUnBlockUserId;

	public void setUserName(String userName){
		this.mUserName = userName;
	}

	public String getUserName(){
		return mUserName;
	}

	public void setUnBlockUserId(String unBlockUserId){
		this.mUnBlockUserId = unBlockUserId;
	}

	public String getUnBlockUserId(){
		return mUnBlockUserId;
	}
}