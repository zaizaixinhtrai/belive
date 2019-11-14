package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 10/3/2016.
 */

public class VersionResponseModel {

    @SerializedName("IsForceUpdate")
    @Expose
    private boolean mIsForceUpdate;
    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("CountryCode")
    @Expose
    private String mCountryCode;

    public boolean getForceUpdate() {
        return mIsForceUpdate;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

}
