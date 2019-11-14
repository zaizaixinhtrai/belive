package com.domain.models;

import com.google.gson.annotations.SerializedName;

public class NextBonusInformationModel {

    @SerializedName("NextTimeSeconds")
    public int nextTimeSeconds;

    @SerializedName("Item")
    public Item item;

    @SerializedName("RemainingDays")
    public int remainingDays;

    @SerializedName("RemainingDaySeconds")
    public int remainingDaySeconds;

    public static class Item {
        @SerializedName("Description")
        public String description;

        @SerializedName("Title")
        public String title;

        @SerializedName("Id")
        public int id;
    }
}