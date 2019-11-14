package com.data.di;

import com.data.repository.Remote;
import com.data.repository.TransactionDataRepository;
import com.data.repository.datasource.TransactionDataSource;
import com.data.repository.datasource.cloud.CloudTransactionDataSource;
import com.domain.repository.TransactionRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thanhbc on 3/17/18.
 */

@Module
public class TransactionRepositoryModule {

    @Provides
    @Remote
    public TransactionDataSource provideRemoteDataSource(CloudTransactionDataSource cloudTransactionDataSource) {
        return cloudTransactionDataSource;
    }

    @Provides
    TransactionRepository provideTransactionRepository(TransactionDataRepository transactionDataRepository) {
        return transactionDataRepository;
    }
}
