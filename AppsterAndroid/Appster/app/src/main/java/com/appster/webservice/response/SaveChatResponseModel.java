package com.appster.webservice.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/11/2015.
 */

public class SaveChatResponseModel {
    @SerializedName("UnreadMessageCount") @Expose
    private int mUnreadMessageCount;

    public int getUnread_message_count() {
        return mUnreadMessageCount;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.mUnreadMessageCount = unread_message_count;
    }


}
