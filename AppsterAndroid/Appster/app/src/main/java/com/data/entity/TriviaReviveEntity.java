package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 3/8/18.
 */

public class TriviaReviveEntity {
    /**
     * ReviveCount : 0
     * CanUseRevive : true
     * Message : string
     * MessageTitle : string
     */

    @SerializedName("ReviveCount")
    public int reviveCount;
    @SerializedName("CanUseRevive")
    public boolean canUseRevive;
    @SerializedName("Message")
    public String message;
    @SerializedName("MessageTitle")
    public String messageTitle;
    @SerializedName("CancelMessageTitle")
    public String cancelMessageTitle = "You're out of the game!";
    @SerializedName("CancelMessage")
    public String cancelMessage = "That was a good try! You can still watch the show though!";
    @SerializedName("ReviveAnim")
    public boolean reviveAnim;
}
