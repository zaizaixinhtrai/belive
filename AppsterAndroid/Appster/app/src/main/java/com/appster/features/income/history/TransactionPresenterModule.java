package com.appster.features.income.history;

import com.data.di.TransactionRepositoryModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thanhbc on 3/17/18.
 */


@Module( includes = {TransactionRepositoryModule.class})
public class TransactionPresenterModule {


    @Provides
    public TransactionHistoryContract.View provideView(TransactionHistoryActivity historyActivity){
        return historyActivity;
    }
    @Provides
    public TransactionHistoryContract.UserActions providePresenter(TransactionHistoryPresenter historyPresenter){
        return historyPresenter;
    }
}

