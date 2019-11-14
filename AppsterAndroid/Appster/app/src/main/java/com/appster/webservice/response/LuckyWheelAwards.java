package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LuckyWheelAwards {

	@SerializedName("VotingLevelId")
	@Expose
	public int votingLevelId;

	@SerializedName("AwardTypeName")
	@Expose
	public String awardTypeName;

	@SerializedName("CreditAmount")
	@Expose
	public int creditAmount;

	@SerializedName("OrderIndex")
	@Expose
	public int orderIndex;

	@SerializedName("Id")
	@Expose
	public int id;

	/*
	1:gem, 2:start, 3:both
	 */
	@SerializedName("CreditType")
	@Expose
	public int creditType;

	@SerializedName("Image")
	@Expose
	public String image;

	/*
	1:host, 2:viewer
	 */
	@SerializedName("AwardType")
	@Expose
	public int awardType;

	@SerializedName("Color")
	@Expose
	public String colorCode;

	public String getCreditType(){
		return creditType==1?"GEMS":creditType==2?"STARS":"GEMS & STARS";
	}
}