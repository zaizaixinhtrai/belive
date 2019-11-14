package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 10/26/17.
 */

public class TotalCashEntity {
    @SerializedName("TotalCash")
    public double totalCash;
    @SerializedName("Currency")
    public String currency;
}
