package com.appster.domain;

import com.appster.webservice.response.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WithdrawnItemModel extends BaseResponse{

	@SerializedName("TotalBean")
	@Expose
	public int totalBean;

	@SerializedName("TotalGold")
	@Expose
	public int totalGold;
}