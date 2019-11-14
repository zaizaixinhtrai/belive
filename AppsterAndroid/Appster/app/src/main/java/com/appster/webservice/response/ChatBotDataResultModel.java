package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanhBan on 9/7/2016.
 */
public class ChatBotDataResultModel {

    @SerializedName("BeginStream")
    @Expose
    public String beginStream;

    @SerializedName("CurrentTime")
    @Expose
    public String currentTime;
    @SerializedName("RepeatTime")
    @Expose
    public int repeatTime;

    @SerializedName("CurrentIndex")
    @Expose
    public int currentIndex;

    @SerializedName("Result")
    @Expose
    public List<ChatBotUserModel> botUsers = new ArrayList<>();
}
