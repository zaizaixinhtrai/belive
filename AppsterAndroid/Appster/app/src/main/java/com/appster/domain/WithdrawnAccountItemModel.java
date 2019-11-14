package com.appster.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WithdrawnAccountItemModel {

	@SerializedName("CurrentAccount")
	@Expose
	public boolean currentAccount;

	@SerializedName("AccountId")
	@Expose
	public int accountId;

	@SerializedName("Email")
	@Expose
	public String email;

	@SerializedName("PaypalAccountId")
	@Expose
	public String paypalAccountId;

	@SerializedName("UserId")
	@Expose
	public int userId;

	@SerializedName("FirstName")
	@Expose
	public String firstName;

	@SerializedName("LastName")
	@Expose
	public String lastName;

	@SerializedName("Mobile")
	@Expose
	public String mobile;

	@SerializedName("Created")
	@Expose
	public String created;
}