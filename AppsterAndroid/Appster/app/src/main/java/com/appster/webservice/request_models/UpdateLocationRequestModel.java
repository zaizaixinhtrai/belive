package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 8/11/2016.
 */
public class UpdateLocationRequestModel {
    @SerializedName("Latitude") @Expose
    private double mLatitude;
    @SerializedName("Longitude") @Expose
    private double mLongitude;

    public UpdateLocationRequestModel(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

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
