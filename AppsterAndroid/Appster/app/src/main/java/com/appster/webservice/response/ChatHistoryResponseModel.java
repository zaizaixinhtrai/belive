package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/11/2015.
 */
public class ChatHistoryResponseModel {

    @SerializedName("Result")
    @Expose
    private ChatHistoryResultModel mResult;
    @SerializedName("NextId")
    @Expose
    private int mNextId;
    @SerializedName("IsEnd")
    @Expose
    private boolean mIsEnd;

    public ChatHistoryResultModel getResult() {
        return mResult;
    }

    public void setResult(ChatHistoryResultModel result) {
        mResult = result;
    }

    public int getNextId() {
        return mNextId;
    }

    public void setNextId(int nextId) {
        mNextId = nextId;
    }

    public boolean isEnd() {
        return mIsEnd;
    }

    public void setEnd(boolean end) {
        mIsEnd = end;
    }
}
