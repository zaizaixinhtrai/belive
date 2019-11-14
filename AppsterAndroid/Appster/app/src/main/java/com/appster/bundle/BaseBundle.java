package com.appster.bundle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.appster.utility.AppsterUtility;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thuc on 16/10/2015.
 */
public class BaseBundle implements Parcelable {
    @SerializedName("key") @Expose
    private String mKey;

    public static final Creator<BaseBundle> CREATOR = new Creator<BaseBundle>() {
        @Override
        public BaseBundle createFromParcel(Parcel in) {
            return new BaseBundle(in);
        }

        @Override
        public BaseBundle[] newArray(int size) {
            return new BaseBundle[size];
        }
    };

    public void setIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getKey(), this);
        intent.putExtras(bundle);
    }

    public void startActivity(Context context, String activityName) {
        AppsterUtility.goToActivity(context, activityName, this);
    }

    public void startActivityForResult(Context context, String activityName, int requestCode) {
        AppsterUtility.goToActivityForResult(context, activityName, this, requestCode);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public BaseBundle() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
    }

    protected BaseBundle(Parcel in) {
        this.mKey = in.readString();
    }

}
