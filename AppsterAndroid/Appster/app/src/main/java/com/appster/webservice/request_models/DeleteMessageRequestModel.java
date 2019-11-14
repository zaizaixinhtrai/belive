package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/9/2015.
 */
public class DeleteMessageRequestModel {
    @SerializedName("ReceiverUserId") @Expose
    private String mReceiverUserId;

    public String getReceiver_user_id() {
        return mReceiverUserId;
    }

    public void setReceiver_user_id(String receiver_user_id) {
        this.mReceiverUserId = receiver_user_id;
    }
}
