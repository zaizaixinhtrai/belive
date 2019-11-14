package com.appster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linh on 25/05/2017.
 */

public class StreamTitleSticker{
    @SerializedName("x") @Expose
    public float mStreamTitleStickerX;
    @SerializedName("y") @Expose
    public float mStreamTitleStickerY;
    @SerializedName("content") @Expose
    public String mStreamTitleStickerContent;
    @SerializedName("streamTitleColor") @Expose
    public String mStreamTitleColorCode = "";
}
