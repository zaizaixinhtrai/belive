package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawnRequestModel{

	@SerializedName("PaymentExchangeRateId")
	@Expose
	private int paymentExchangeRateId;

	@SerializedName("Email")
	@Expose
	private String email;

	@SerializedName("PaypalAccountId")
	@Expose
	private String paypalAccountId;

	@SerializedName("FirstName")
	@Expose
	private String firstName;

	@SerializedName("LastName")
	@Expose
	private String lastName;

	@SerializedName("Mobile")
	@Expose
	private String mobile;

	public void setPaymentExchangeRateId(int paymentExchangeRateId){
		this.paymentExchangeRateId = paymentExchangeRateId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public void setPaypalAccountId(String paypalAccountId){
		this.paypalAccountId = paypalAccountId;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}
}