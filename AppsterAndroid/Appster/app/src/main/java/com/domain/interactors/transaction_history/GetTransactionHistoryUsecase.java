package com.domain.interactors.transaction_history;

import com.appster.core.adapter.DisplayableItem;
import com.domain.interactors.UseCase;
import com.domain.models.BasePagingModel;
import com.domain.repository.TransactionRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by thanhbc on 10/26/17.
 */

public class GetTransactionHistoryUsecase extends UseCase<BasePagingModel<DisplayableItem>,GetTransactionHistoryUsecase.Params> {
    private final TransactionRepository mTransactionRepository;

    @Inject
    public GetTransactionHistoryUsecase(TransactionRepository transactionRepository, @Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mTransactionRepository = transactionRepository;
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> buildObservable(Params params) {
        return mTransactionRepository.getTransactions(params.nextId,params.startPage);
    }

    public static final class Params {
        final int startPage;
        final int nextId;

        private Params(int startPage, int nextId) {
            this.startPage = startPage;
            this.nextId = nextId;
        }

        public static Params loadPage(int startPage, int nextId) {
            return new Params(startPage, nextId);
        }
    }
}
