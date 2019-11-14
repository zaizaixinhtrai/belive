package com.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaFinishEntity {

    /**
     * Win : true
     * WinnerCount : 0
     * Prize : 0
     * PrizePerUserString : string
     * Message : string
     */

    @SerializedName("Win")
    public boolean win;
    @SerializedName("WinnerCount")
    public int winnerCount;
    @SerializedName("Prize")
    public int prize;
    @SerializedName("PrizePerUserString")
    public String prizePerUserString;
    @SerializedName("Message")
    public String message;
    @SerializedName("WinnerPopup")
    public WinnerPopup winnerPopup;

    static public class WinnerPopup {
        @SerializedName("Title")
        public String title;
        @SerializedName("PrizeMessage")
        public String prizeMessage;
        @SerializedName("Message")
        public String message;
    }
}
