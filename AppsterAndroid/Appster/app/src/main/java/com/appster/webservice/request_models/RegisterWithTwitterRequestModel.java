package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by User on 12/15/2016.
 */

public class RegisterWithTwitterRequestModel extends BaseRegisterRequestModel {
    @SerializedName("TwitterId") @Expose
    private String mTwitterId;

    public RegisterWithTwitterRequestModel(String username,
                                           String displayname,
                                           String TwitterId,
                                           int device_type,
                                           String device_udid,
                                           String device_token,
                                           double latitude,
                                           double longitude,
                                           String address,
                                           String email,
                                           File profile,
                                           String ref_id,
                                           String Gender,String deviceName, String OSVersion,String version) {
        super(username, displayname, device_type, device_udid, device_token, latitude, longitude, address, email, profile, ref_id, Gender,deviceName,OSVersion,version);
        this.mTwitterId = TwitterId;
        handleAddPartData();
    }




    public String getInstagramId() {
        return mTwitterId;
    }

    public void setInstagramId(String instagramId) {
        mTwitterId = instagramId;
    }

    @Override
    protected void handleAddPartData() {
        super.handleAddPartData();
        addPartNotEmptyString("TwitterId", mTwitterId);
    }
}
