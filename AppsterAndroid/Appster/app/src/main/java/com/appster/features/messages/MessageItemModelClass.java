package com.appster.features.messages;

import com.appster.core.adapter.DisplayableItem;
import com.google.gson.annotations.SerializedName;

public class MessageItemModelClass implements DisplayableItem {

    @SerializedName("UserId")
    private String msg_user_id = "";
    @SerializedName("Message")
    private String msg_message = "";
    @SerializedName("Timestamp")
    private String msg_timestamp = "";
    @SerializedName("DisplayName")
    private String msg_display_name = "";
    @SerializedName("UserImage")
    private String msg_profile_pic = "";
    @SerializedName("UserName")
    private String msg_user_name = "";
    @SerializedName("MessageId")
    private String msg_message_id = "";
    @SerializedName("SenderId")
    private String msg_sender_id = "";
    @SerializedName("Gender")
    private String gender = "";
    @SerializedName("UnreadStatus")
    private int msg_unread_status ;
    @SerializedName("UnreadMessageCount")
    private int unread_message_count;
    private int MessageType;
    private String Created;

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getUnread_message_count() {
        return unread_message_count;
    }

    public void setUnread_message_count(int unread_message_count) {
        this.unread_message_count = unread_message_count;
    }

    public int getMsg_unread_status() {
        return msg_unread_status;
    }

    public void setMsg_unread_status(int msg_unread_status) {
        this.msg_unread_status = msg_unread_status;
    }

    public String getMsg_sender_id() {
        return msg_sender_id;
    }

    public void setMsg_sender_id(String msg_sender_id) {
        this.msg_sender_id = msg_sender_id;
    }

    public String getMsg_message_id() {
        return msg_message_id;
    }

    public void setMsg_message_id(String msg_message_id) {
        this.msg_message_id = msg_message_id;
    }

    public String getMsg_user_name() {
        return msg_user_name;
    }

    public void setMsg_user_name(String msg_user_name) {
        this.msg_user_name = msg_user_name;
    }

    public String getMsg_user_id() {
        return msg_user_id;
    }

    public void setMsg_user_id(String msg_user_id) {
        this.msg_user_id = msg_user_id;
    }

    public String getMsg_message() {
        return msg_message;
    }

    public void setMsg_message(String msg_message) {
        this.msg_message = msg_message;
    }

    public String getMsg_timestamp() {
        return msg_timestamp;
    }

    public void setMsg_timestamp(String msg_timestamp) {
        this.msg_timestamp = msg_timestamp;
    }

    public String getMsg_display_name() {
        return msg_display_name;
    }

    public void setMsg_display_name(String msg_display_name) {
        this.msg_display_name = msg_display_name;
    }

    public String getMsg_profile_pic() {
        return msg_profile_pic;
    }

    public void setMsg_profile_pic(String msg_profile_pic) {
        this.msg_profile_pic = msg_profile_pic;
    }


}
