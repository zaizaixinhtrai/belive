package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by Jeremy on 14/12/2016.
 */

public class RegisterWithWeiboRequestModel extends BaseRegisterRequestModel {
    @SerializedName("WeiboId") @Expose
    private String mWeiboId;

    public RegisterWithWeiboRequestModel(String username,
                                             String displayname,
                                             String weiboId,
                                             int device_type,
                                             String device_udid,
                                             String device_token,
                                             double latitude,
                                             double longitude,
                                             String address,
                                             String email,
                                             File profile,
                                             String ref_id,
                                             String Gender,
                                             String deviceName,
                                             String OSVersion,
                                             String version) {
        super(username, displayname, device_type, device_udid, device_token, latitude, longitude, address, email, profile, ref_id, Gender,deviceName,OSVersion,version);
        this.mWeiboId = weiboId;
        handleAddPartData();
    }


    public String getWeiboId() {
        return mWeiboId;
    }

    public void setWeiboId(String weiboId) {
        mWeiboId = weiboId;
    }

    @Override
    protected void handleAddPartData() {
        super.handleAddPartData();
        addPartNotEmptyString("WeiboId", mWeiboId);
    }


}
