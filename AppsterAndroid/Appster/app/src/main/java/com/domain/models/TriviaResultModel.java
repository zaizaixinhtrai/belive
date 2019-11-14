package com.domain.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaResultModel {
    public int participant = 0;
    public boolean isCorrectAnswer = false;
    public List<TriviaAnswers> answers = new ArrayList<>();
    public int previousRevivedCount = 0;
    public String message;
    public static class TriviaAnswers {
        public boolean isAnswer = false;
        public int chosenNum = 0;
        public int optionId;

        public TriviaAnswers(boolean isAnswer, int chosenNum, int optionId) {
            this.isAnswer = isAnswer;
            this.chosenNum = chosenNum;
            this.optionId = optionId;
        }

    }
}
