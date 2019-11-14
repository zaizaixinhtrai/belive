package com.appster.webservice.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/12/2015.
 */
public class SettingFeaturesRequestModel {
    @SerializedName("user_id") @Expose
    public String mUserId;
    @SerializedName("VoiceCall") @Expose
    public int mVoiceCall;
    @SerializedName("NearbyFeature") @Expose
    public int mNearbyFeature;
    @SerializedName("Searchable") @Expose
    public int mSearchable;
    @SerializedName("NotificationSound") @Expose
    public int mNotificationSound;
    @SerializedName("Notification") @Expose
    public int mNotification;
    @SerializedName("LiveNotification") @Expose
    public int liveNotification;
    @SerializedName("HideMessageDetails") @Expose
    public int mHideMessageDetails;
    @SerializedName("VideoCall") @Expose
    public int mVideoCall;
    @SerializedName("Messaging") @Expose
    public int mMessaging;
    @SerializedName("Language") @Expose
    public int mLanguage;
}
