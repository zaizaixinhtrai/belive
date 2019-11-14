package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by Jeremy on 14/12/2016.
 */

public class RegisterWithWeChatRequestModel extends BaseRegisterRequestModel {
    @SerializedName("WeChatId") @Expose
    private String mWeChatId;

    public RegisterWithWeChatRequestModel(String username,
                                         String displayname,
                                         String weChatId,
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
        this.mWeChatId = weChatId;
        handleAddPartData();
    }


    public String getWeChatId() {
        return mWeChatId;
    }

    public void setWeChatId(String weChatId) {
        mWeChatId = weChatId;
    }

    @Override
    protected void handleAddPartData() {
        super.handleAddPartData();
        addPartNotEmptyString("WeChatId", mWeChatId);
    }


}
