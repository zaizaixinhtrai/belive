package com.domain.interactors.trivia;

import com.appster.core.adapter.DisplayableItem;
import com.domain.interactors.UseCase;
import com.domain.models.BasePagingModel;
import com.domain.repository.TriviaRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by Ngoc on 3/14/2018.
 */

public class TriviaWinnerListUseCase extends UseCase<BasePagingModel<DisplayableItem>, TriviaWinnerListUseCase.Params> {


    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaWinnerListUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> buildObservable(TriviaWinnerListUseCase.Params params) {
        return mTriviaRepository.getTriviaWinnerList(params.nextId, params.limit, params.triviaId);
    }

    public static final class Params {
        final int nextId;

        final int limit;

        final int triviaId;

        public Params(int nextId, int limit, int triviaId) {
            this.nextId = nextId;
            this.limit = limit;
            this.triviaId = triviaId;
        }

        public static Params byType(int nextId, int limit, int triviaId) {
            return new Params(nextId, limit, triviaId);
        }
    }
}
