package com.domain.interactors.trivia;

import com.domain.interactors.UseCase;
import com.domain.models.TriviaInfoModel;
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

public class TriviaInfoUseCase extends UseCase<TriviaInfoModel,TriviaInfoUseCase.Params> {
    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaInfoUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }

    @Override
    public Observable<TriviaInfoModel> buildObservable(TriviaInfoUseCase.Params params) {
        return mTriviaRepository.getTriviaViewerInfo(params.triviaId);
    }

    public final static class Params {

        final int triviaId;
        private Params(int triviaId) {
            this.triviaId=triviaId;
        }

        public static Params load(int triviaId) {
            return new Params(triviaId);
        }
    }
}
