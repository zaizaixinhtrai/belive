package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockUserRequestModel{

	@SerializedName("UserName")
	@Expose
	private String mUserName;

	@SerializedName("BlockUserId")
	@Expose
	private String mBlockUserId;

	@SerializedName("Reason")
	@Expose
	private String mReason;

	public void setUserName(String userName){
		this.mUserName = userName;
	}

	public String getUserName(){
		return mUserName;
	}

	public void setBlockUserId(String blockUserId){
		this.mBlockUserId = blockUserId;
	}

	public String getBlockUserId(){
		return mBlockUserId;
	}

	public void setReason(String reason){
		this.mReason = reason;
	}

	public String getReason(){
		return mReason;
	}
}