package com.domain.interactors.trivia;

import com.domain.interactors.UseCase;
import com.domain.repository.TriviaRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaAnswerUseCase extends UseCase<Boolean,TriviaAnswerUseCase.Params> {
    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaAnswerUseCase(@Named(UI) Scheduler uiThread, @Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }


    @Override
    public Observable<Boolean> buildObservable(Params params) {
        return mTriviaRepository.getTriviaAnswer(params.triviaId,params.questionId,params.optionId);
    }

    public static final class Params {
        final int triviaId;
        final int questionId;
        final int optionId;

        private Params(int triviaId, int questionId, int optionId) {
            this.triviaId = triviaId;
            this.questionId = questionId;
            this.optionId = optionId;
        }

        public static Params answer(int triviaId,int questionId,int optionId){
            return new Params(triviaId,questionId,optionId);
        }
    }
}
