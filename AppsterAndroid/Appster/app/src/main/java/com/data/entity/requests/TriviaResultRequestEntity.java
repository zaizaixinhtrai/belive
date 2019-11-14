package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/24/18.
 */

public class TriviaResultRequestEntity {
    @SerializedName("TriviaId")
    private final int triviaId;
    @SerializedName("QuestionId")
    private final int questionId;
    public TriviaResultRequestEntity(int triviaId, int questionId) {
        this.triviaId = triviaId;
        this.questionId = questionId;

    }
}
