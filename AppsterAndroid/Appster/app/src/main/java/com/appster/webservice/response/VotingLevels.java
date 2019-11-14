package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VotingLevels{

	@SerializedName("FromCredit")
	@Expose
	public int fromCredit;

	@SerializedName("ToCredit")
	@Expose
	public int toCredit;

	@SerializedName("OrderIndex")
	@Expose
	public int orderIndex;

	@SerializedName("Id")
	@Expose
	public int id;

	@SerializedName("Tittle")
	@Expose
	public String tittle;
}