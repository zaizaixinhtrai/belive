package com.domain.interactors.transaction_history;

import com.domain.interactors.UseCase;
import com.domain.models.TotalCashoutModel;
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

public class GetTotalCashoutAmountUsecase extends UseCase<TotalCashoutModel,String> {
    private final TransactionRepository mTransactionRepository;
    @Inject
    public GetTotalCashoutAmountUsecase(TransactionRepository transactionRepository, @Named(UI) Scheduler uiThread,@Named(IO) Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mTransactionRepository = transactionRepository;
    }

    @Override
    public Observable<TotalCashoutModel> buildObservable(String string) {
        return mTransactionRepository.getTotalCash();
    }
}
