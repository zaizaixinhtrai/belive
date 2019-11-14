package com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaResultEntity {

    /**
     * Title : string
     * Correct : true
     * Options : [{"OptionId":0,"Option":"string","IsAnswer":true,"Count":0}]
     */

    @SerializedName("Title")
    public String title;
    @SerializedName("Correct")
    public boolean correct;
    @SerializedName("Options")
    public List<Options> options;

    @SerializedName("Participants")
    public int participants = 0;
    @SerializedName("PreviousRevivedCount")
    public int previousRevivedCount = 0;
    @SerializedName("Message")
    public String message;
    public static class Options {
        /**
         * OptionId : 0
         * Option : string
         * IsAnswer : true
         * Count : 0
         */

        @SerializedName("OptionId")
        public int optionId;
        @SerializedName("Option")
        public String option;
        @SerializedName("IsAnswer")
        public boolean isAnswer;
        @SerializedName("Count")
        public int count;
    }
}
