package com.appster.webservice.request_models;

import android.os.Parcel;

import com.appster.bundle.BaseBundle;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by USER on 10/29/2015.
 */
public class EditPostRequestModel extends BaseBundle{
    @SerializedName("Address")
    @Expose
    private String mAddress;

    @SerializedName("Title")
    @Expose
    private String mTitle;

    @SerializedName("Latitude")
    @Expose
    private double mLatitude;

    @SerializedName("Longitude")
    @Expose
    private double mLongitude;

    @SerializedName("PostId")
    @Expose
    private int mPostId;

    @SerializedName("TagUsers") @Expose
    private String mTaggedUsers;

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }


    public void setPost_id(int post_id) {
        this.mPostId = post_id;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }


    public String getAddress() {
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public int getPost_id() {
        return mPostId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTaggedUsers() {
        return mTaggedUsers;
    }

    public void setTaggedUsers(String taggedUsers) {
        mTaggedUsers = taggedUsers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mAddress);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeInt(this.mPostId);
        dest.writeString(this.mTitle);
        dest.writeString(mTaggedUsers);
    }

    public EditPostRequestModel() {
    }

    protected EditPostRequestModel(Parcel in) {
        super(in);
        this.mAddress = in.readString();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mPostId = in.readInt();
        this.mTitle = in.readString();
        in.writeString(mTaggedUsers);
    }

    public static final Creator<EditPostRequestModel> CREATOR = new Creator<EditPostRequestModel>() {
        public EditPostRequestModel createFromParcel(Parcel source) {
            return new EditPostRequestModel(source);
        }

        public EditPostRequestModel[] newArray(int size) {
            return new EditPostRequestModel[size];
        }
    };
}
