package com.domain.models;

/**
 * Created by thanhbc on 10/26/17.
 */

public class TotalCashoutModel {
    public double totalCashout;
    public String currency;

    public TotalCashoutModel(double totalCashout,String currency) {
        this.totalCashout = totalCashout;
        this.currency = currency;
    }
}
