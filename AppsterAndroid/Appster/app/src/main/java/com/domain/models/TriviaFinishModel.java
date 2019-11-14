package com.domain.models;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaFinishModel {

    public int winnerCount;
    public String prizePerUserString;
    public String message;
    public boolean win;
    public WinnerPopup winnerPopup;

    public static class WinnerPopup {
        public String title;
        public String prizeMessage;
        public String message;
    }
}
