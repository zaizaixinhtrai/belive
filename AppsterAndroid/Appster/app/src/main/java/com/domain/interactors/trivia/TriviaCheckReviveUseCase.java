package com.domain.interactors.trivia;

import com.domain.interactors.UseCase;
import com.domain.models.TriviaReviveModel;
import com.domain.repository.TriviaRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by thanhbc on 3/8/18.
 */

public class TriviaCheckReviveUseCase extends UseCase<TriviaReviveModel, TriviaCheckReviveUseCase.Params> {

    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaCheckReviveUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }

    @Override
    public Observable<TriviaReviveModel> buildObservable(Params params) {
        return mTriviaRepository.checkTriviaRevise(params.triviaId, params.questionId);
    }

    public static final class Params {
        final int triviaId;
        final int questionId;

        private Params(int triviaId, int questionId) {
            this.triviaId = triviaId;
            this.questionId = questionId;
        }


        public static Params checkTrivia(int triviaId, int questionId) {
            return new Params(triviaId, questionId);
        }
    }
}
