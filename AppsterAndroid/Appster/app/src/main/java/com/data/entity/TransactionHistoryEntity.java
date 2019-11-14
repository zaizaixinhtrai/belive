package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 10/26/17.
 */

public class TransactionHistoryEntity {
    @SerializedName("TransactionTime")
    public String transactionTime;
    @SerializedName("StarsDeducted")
    public int starsDeducted;
    @SerializedName("ConvertedValue")
    public double convertedValue;
    @SerializedName("Currency")
    public String currency;
    @SerializedName("Status")
    public int status;
    @SerializedName("IsTriviaCashOut")
    public boolean isTriviaCashOut;
}
