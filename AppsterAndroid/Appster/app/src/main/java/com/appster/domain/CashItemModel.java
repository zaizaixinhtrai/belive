package com.appster.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashItemModel{

	@SerializedName("PaymentExchangeRateId")
	@Expose
	public int paymentExchangeRateId;

	@SerializedName("Gold")
	@Expose
	public int gold;

	@SerializedName("Money")
	@Expose
	public int money;

	@SerializedName("Desciption")
	@Expose
	public String desciption;

	@SerializedName("Currentcy")
	@Expose
	public String currentcy;

	@SerializedName("GoogleFormUrl")
	@Expose
	public String paymentUrl;


}