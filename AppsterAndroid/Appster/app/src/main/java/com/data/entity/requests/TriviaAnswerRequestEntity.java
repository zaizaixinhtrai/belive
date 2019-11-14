package com.data.entity.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thanhbc on 2/24/18.
 */

public class TriviaAnswerRequestEntity {
    @SerializedName("TriviaId")
    private final int triviaId;
    @SerializedName("QuestionId")
    private final int questionId;
    @SerializedName("AnswerId")
    private final int answerId;

    public TriviaAnswerRequestEntity(int triviaId, int questionId, int answerId) {
        this.triviaId = triviaId;
        this.questionId = questionId;
        this.answerId = answerId;
    }
}
