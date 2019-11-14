package com.domain.interactors.trivia;

import com.domain.interactors.UseCase;
import com.domain.models.TriviaFinishModel;
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

public class TriviaFinishUseCase extends UseCase<TriviaFinishModel,TriviaFinishUseCase.Params> {
    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaFinishUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }


    @Override
    public Observable<TriviaFinishModel> buildObservable(Params params) {
        return mTriviaRepository.getTriviaFinish(params.triviaId);
    }

    public static final class Params {
        final int triviaId;

        private Params(int triviaId) { this.triviaId = triviaId; }

        public static Params finish(int triviaId) { return new Params(triviaId); }
    }
}
