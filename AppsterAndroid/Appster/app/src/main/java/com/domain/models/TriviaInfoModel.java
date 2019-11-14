package com.domain.models;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaInfoModel {

    public int triviaId;

    public int secsToBegin;

    public int secsToGetTriviaQuestionsApi;

    public long nextQuestionDateTime;

    public int nextQuestionId;

    public int questionWaitingTime;

    public int answerTime;

    public int resultWaitingTime;

    public int resultTime;

    public int finishTime;

    public boolean canPlay;

    public boolean isRejoin;

    public int finishWaitingTime;

    public int diffInSec = 0;

    public String hash;
    public List<Questions> questions = new ArrayList<>();
    public String message;
    public String messageTitle;
    public int reviveWaitingTime;
    public int reviveCount;
    public int nextQuestionIndex;
    public boolean reviveAnim;
    public String countryCode;

    public Questions  getNextQuestion() {
        Questions question;
        Timber.e("nextQuestionId %s", nextQuestionId);
        if (!questions.isEmpty()) {
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).questionId == nextQuestionId) {
                    nextQuestionIndex = i + 1;
                    //update nextquestion Id
                    this.nextQuestionId = nextQuestionIndex < questions.size() ? questions.get(nextQuestionIndex).questionId : -1;
                    //get current question
                    question = questions.get(i);
                    return question;
                }
            }
        }
        return null;
    }

    public boolean hasEndedQuestions() {
        return questions == null || questions.isEmpty() || nextQuestionIndex >= questions.size();
    }

    public static class Questions {
        public String title;
        public int questionId;
        public List<Options> options;

        public Questions(String title, int questionId, List<Options> options) {
            this.title = title;
            this.questionId = questionId;
            this.options = options;
        }

        @Override
        public String toString() {
            return "Questions{" +
                    "title='" + title + '\'' +
                    ", questionId=" + questionId +
                    ", options=" + options +
                    '}';
        }

        public static class Options {
            public int optionId;
            public String option;

            public Options(int optionId, String option) {
                this.optionId = optionId;
                this.option = option;
            }

            @Override
            public String toString() {
                return "Options{" +
                        "optionId=" + optionId +
                        ", option='" + option + '\'' +
                        '}';
            }
        }
    }
}
