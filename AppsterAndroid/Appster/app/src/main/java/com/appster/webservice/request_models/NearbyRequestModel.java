package com.appster.webservice.request_models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/12/2015.
 */
public class NearbyRequestModel extends BasePagingRequestModel {
    @SerializedName("Latitude") @Expose
    private double mLatitude;
    @SerializedName("Longitude") @Expose
    private double mLongitude;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double Latitude) {
        this.mLatitude = Latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double Longitude) {
        this.mLongitude = Longitude;
    }
}
