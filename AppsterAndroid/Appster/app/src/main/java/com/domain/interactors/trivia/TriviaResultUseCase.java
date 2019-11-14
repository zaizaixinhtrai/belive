package com.domain.interactors.trivia;

import com.domain.interactors.UseCase;
import com.domain.models.TriviaResultModel;
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

public class TriviaResultUseCase extends UseCase<TriviaResultModel, TriviaResultUseCase.Params> {
    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaResultUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }

    @Override
    public Observable<TriviaResultModel> buildObservable(Params params) {
        return mTriviaRepository.getTriviaResult(params.triviaId,params.questionId);
    }

    public static final class Params {
        final int triviaId;
        final int questionId;

        private Params(int triviaId, int questionId) {
            this.triviaId = triviaId;
            this.questionId = questionId;
        }

        public static Params result(int triviaId, int questionId) {
            return new Params(triviaId, questionId);
        }
    }
}
