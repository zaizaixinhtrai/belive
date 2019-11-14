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
 * Created by Ngoc on 3/9/2018.
 */

public class TriviaRankingListUseCase extends UseCase<BasePagingModel<DisplayableItem>, TriviaRankingListUseCase.Params> {


    private final TriviaRepository mTriviaRepository;

    @Inject
    public TriviaRankingListUseCase(@Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread, TriviaRepository triviaRepository) {
        super(uiThread, executorThread);
        mTriviaRepository = triviaRepository;
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> buildObservable(Params params) {
        return mTriviaRepository.getTriviaRankingList(params.triviaRankingType, params.nextId, params.limit);
    }

    public static final class Params {
        final int triviaRankingType;
        final int nextId;
        final int limit;

        private Params(int triviaRankingType, int nextId, int limit) {
            this.triviaRankingType = triviaRankingType;
            this.nextId = nextId;
            this.limit = limit;
        }

        public static Params byRankingType(int triviaRankingType, int nextId, int limit) {
            return new Params(triviaRankingType, nextId, limit);
        }
    }
}
