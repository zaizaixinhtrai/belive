package com.appster.models;

import com.google.gson.annotations.SerializedName;

public class DailyTopFanModel{

	@SerializedName("UserName")
	public String userName;

	@SerializedName("GiftGold")
	public int giftGold;

	@SerializedName("DisplayName")
	public String displayName;

	@SerializedName("GiftGoldString")
	public String giftGoldString;

	@Override
 	public String toString(){
		return 
			"DailyTopFanModel{" + 
			"userName = '" + userName + '\'' + 
			",giftGold = '" + giftGold + '\'' + 
			",displayName = '" + displayName + '\'' + 
			",giftGoldString = '" + giftGoldString + '\'' + 
			"}";
		}
}