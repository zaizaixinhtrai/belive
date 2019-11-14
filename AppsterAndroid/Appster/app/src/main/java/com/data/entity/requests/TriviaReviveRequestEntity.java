package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 3/13/18.
 */

public class TriviaReviveRequestEntity {
    @SerializedName("TriviaId")
    private final int id;
    @SerializedName("QuestionId")
    private final int questionId;
    public TriviaReviveRequestEntity(int triviaId, int questionId) {
        this.id = triviaId;
        this.questionId = questionId;
    }
}
