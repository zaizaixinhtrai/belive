package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ThanhBan on 9/20/2016.
 */
public class BotSendGiftRequestModel {
    @SerializedName("GiftId") @Expose
    private int mGiftId;
    @SerializedName("BotId") @Expose
    private int mBotId;
    @SerializedName("StreamId") @Expose
    private int mStreamId;

    public int getGift_id() {
        return mGiftId;
    }

    public void setGift_id(int gift_id) {
        this.mGiftId = gift_id;
    }


    public int getReceiver_user_id() {
        return mBotId;
    }

    public void setBot_id(int bot_id) {
        this.mBotId = bot_id;
    }

    public int getStream_id() {
        return mStreamId;
    }

    public void setStream_id(int stream_id) {
        this.mStreamId = stream_id;
    }
}
