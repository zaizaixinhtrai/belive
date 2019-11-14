package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ngoc on 3/14/2018.
 */

public class TriviaWinnerListRequestEntity {
    /**
     * NextId : 0
     * Limit : 0
     * TriviaId : 0
     * DebugMinCorrect : 0
     */
    @SerializedName("NextId")
    public int NextId;

    @SerializedName("Limit")
    public int Limit;

    @SerializedName("TriviaId")
    public int TriviaId;

    public TriviaWinnerListRequestEntity(int nextId, int limit, int triviaId) {
        NextId = nextId;
        Limit = limit;
        TriviaId = triviaId;
    }
}
