package com.appster.refill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefillListItem {

    @SerializedName("TopupId")
    @Expose
    private String TopupId;
    @SerializedName("StoreId")
    @Expose
    private String StoreId;
    @SerializedName("TopupImage")
    @Expose
    private String TopupImage;
    @SerializedName("PriceUsd")
    @Expose
    private String PriceUsd;
    @SerializedName("PriceCny")
    @Expose
    private String PriceCny;
    @SerializedName("Percentage")
    @Expose
    private int Percentage;
    @SerializedName("Bean")
    @Expose
    private int Bean;
    @SerializedName("Name")
    @Expose
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getBean() {
        return Bean;
    }

    public void setBean(int bean) {
        this.Bean = bean;
    }

    public String getId() {
        return TopupId;
    }

    public void setId(String id) {
        this.TopupId = id;
    }

    public String getAndroid_store_id() {
        return StoreId;
    }

    public void setAndroid_store_id(String android_store_id) {
        this.StoreId = android_store_id;
    }

    public String getImage() {
        return TopupImage;
    }

    public void setImage(String image) {
        this.TopupImage = image;
    }

    public String getPrice_usd() {
        return PriceUsd;
    }

    public void setPrice_usd(String price_usd) {
        this.PriceUsd = price_usd;
    }

    public String getPrice_cny() {
        return PriceCny;
    }

    public void setPrice_cny(String price_cny) {
        this.PriceCny = price_cny;
    }

    public int getPercentage() {
        return Percentage;
    }

    public void setPercentage(int percentage) {
        this.Percentage = percentage;
    }

    public String getIos_store_id() {
        return StoreId;
    }

    public void setIos_store_id(String ios_store_id) {
        this.StoreId = ios_store_id;
    }
}
