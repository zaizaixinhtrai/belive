package com.appster.webservice.request_models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/15/2015.
 */
public class VerifyIAppPayRequestModel {
    @SerializedName("appId") @Expose
    private String mAppId;
    @SerializedName("userId") @Expose
    private String mUserId;
    @SerializedName("orderId") @Expose
    private String mOrderId;
    @SerializedName("itemId") @Expose
    private String mItemId;
    @SerializedName("paymentAmount") @Expose
    private String mPaymentAmount;
    @SerializedName("paymentCurrency") @Expose
    private String mPaymentCurrency;
    @SerializedName("purchaseTimeMillis") @Expose
    private long mPurchaseTimeMillis;
    @SerializedName("platform") @Expose
    private String mPlatform;
    @SerializedName("signValue") @Expose
    private String mSignValue;

    public String getApp_id() {
        return mAppId;
    }

    public void setApp_id(String app_id) {
        this.mAppId = app_id;
    }

    public String getUser_id() {
        return mUserId;
    }

    public void setUser_id(String user_id) {
        this.mUserId = user_id;
    }

    public String getOrder_id() {
        return mOrderId;
    }

    public void setOrder_id(String order_id) { this.mOrderId = order_id; }

    public String getItem_id() {
        return mItemId;
    }

    public void setItem_id(String item_id) {
        this.mItemId = item_id;
    }

    public String getPayment_amount() {
        return mPaymentAmount;
    }

    public void setPayment_amount(String payment_amount) {
        this.mPaymentAmount = payment_amount;
    }

    public String getPayment_currency() {
        return mPaymentCurrency;
    }

    public void setPayment_currency(String payment_currency) { this.mPaymentCurrency = payment_currency; }

    public long getPurchase_time_in_millis() {
        return mPurchaseTimeMillis;
    }

    public void setPurchase_time_in_millis(long purchase_time_in_millis) { this.mPurchaseTimeMillis = purchase_time_in_millis; }

    public String getPlatform() {
        return mPlatform;
    }

    public void setPlatform(String platform) {
        this.mPlatform = platform;
    }

    public String getSignValue() {
        return mSignValue;
    }

    public void setSignValue(String signValue) {
        this.mSignValue = signValue;
    }

    @Override
    public String toString() {
        return  new Gson().toJson(this);
    }
}
