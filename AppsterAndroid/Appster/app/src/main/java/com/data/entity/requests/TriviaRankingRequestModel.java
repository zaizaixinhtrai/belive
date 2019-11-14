package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 3/13/2018.
 */

public class TriviaRankingRequestModel {
    /**
     * TriviaRankingType : 0
     * NextId : 0
     * Limit : 0
     */

    @SerializedName("TriviaRankingType")
    public final int triviaRankingType;

    @SerializedName("NextId")
    public int nextId;

    @SerializedName("Limit")
    public int limit;

    public TriviaRankingRequestModel(int triviaRankingType, int nextId, int limit) {
        this.triviaRankingType = triviaRankingType;
        this.nextId = nextId;
        this.limit = limit;
    }
}
