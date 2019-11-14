package com.appster.webservice.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11/21/2016.
 */

public class MaintenanceModel implements Parcelable {

    @SerializedName("maintenance_mode")
    @Expose
    public int maintenanceMode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("time")
    @Expose
    public String time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maintenanceMode);
        dest.writeString(this.message);
        dest.writeString(this.time);
    }

    public MaintenanceModel() {
    }

    protected MaintenanceModel(Parcel in) {
        this.maintenanceMode = in.readInt();
        this.message = in.readString();
        this.time = in.readString();
    }

    public static final Parcelable.Creator<MaintenanceModel> CREATOR = new Parcelable.Creator<MaintenanceModel>() {
        @Override
        public MaintenanceModel createFromParcel(Parcel source) {
            return new MaintenanceModel(source);
        }

        @Override
        public MaintenanceModel[] newArray(int size) {
            return new MaintenanceModel[size];
        }
    };
}
