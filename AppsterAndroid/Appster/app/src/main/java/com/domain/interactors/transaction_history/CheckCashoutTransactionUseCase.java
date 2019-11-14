package com.domain.interactors.transaction_history;

import com.appster.webservice.response.BaseResponse;
import com.domain.interactors.UseCase;
import com.domain.repository.TransactionRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by thanhbc on 10/25/17.
 */

public class CheckCashoutTransactionUseCase extends UseCase<BaseResponse<Boolean>,Void> {
    private final TransactionRepository mTransactionRepository;
    @Inject
    public CheckCashoutTransactionUseCase(TransactionRepository transactionRepository, @Named(UI) Scheduler uiThread, @Named(IO) Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mTransactionRepository = transactionRepository;
    }


    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(Void aVoid) {
        return mTransactionRepository.checkCashOutAvailable();
    }
}
