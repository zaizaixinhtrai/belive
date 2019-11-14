package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneLoginRequestModel extends BaseLoginRequestModel {
    @SerializedName("CountryCode") @Expose
    public String countryCode;
    @SerializedName("Phone") @Expose
    public String phoneNumber;
    @SerializedName("Password") @Expose
    public String passWord;
}
