package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 12/27/17.
 */

public class ContactRequestEntity {
    @SerializedName("ContactId")
    public long id;
    @SerializedName("PhoneNumber")
    public String phoneNumber;

    @SerializedName("RawData")
    public String rawData;
    @SerializedName("DoB")
    public String birthDay;
    @SerializedName("Email")
    public String email;
    @SerializedName("Address")
    public String address;

    @SerializedName("CountryCode")
    public String countryCode;

}
