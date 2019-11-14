package com.appster.webservice.response;

import com.appster.message.ChatItemModelClass;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 6/20/2016.
 */
public class ChatHistoryResultModel {
    @SerializedName("UnreadMessageCount")
    @Expose
    private int mUnreadMessageCount;
    @SerializedName("Status")
    @Expose
    private int mStatus;
    @SerializedName("Messaging")
    @Expose
    private int mMessaging;
    @SerializedName("VoiceCall")
    @Expose
    private int mVoiceCall;
    @SerializedName("VideoCall")
    @Expose
    private int mVideoCall;
    @SerializedName("ChatHistory")
    @Expose
    private List<ChatItemModelClass> mChatHistory;

    public List<ChatItemModelClass> getChatHistory() {
        return mChatHistory;
    }

    public void setChatHistory(List<ChatItemModelClass> chatHistory) {
        mChatHistory = chatHistory;
    }

    public int getMessaging() {
        return mMessaging;
    }

    public void setMessaging(int messaging) {
        mMessaging = messaging;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getVideoCall() {
        return mVideoCall;
    }

    public void setVideoCall(int videoCall) {
        mVideoCall = videoCall;
    }

    public int getVoiceCall() {
        return mVoiceCall;
    }

    public void setVoiceCall(int voiceCall) {
        mVoiceCall = voiceCall;
    }

    public int getUnread_message_count() {
        return mUnreadMessageCount;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.mUnreadMessageCount = unread_message_count;
    }
}
