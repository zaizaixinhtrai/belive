package com.data.di;

import com.data.repository.Remote;
import com.data.repository.TriviaDataRepository;
import com.data.repository.datasource.TriviaDataSource;
import com.data.repository.datasource.cloud.CloudTriviaDataSource;
import com.domain.repository.TriviaRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thanhbc on 4/26/18.
 */

@Module
public class TriviaRepositoryModule {

    @Provides
    @Remote
    public TriviaDataSource provideRemoteDataSource(CloudTriviaDataSource cloudTriviaDataSource) {
        return cloudTriviaDataSource;
    }

    @Provides
    TriviaRepository provideTransactionRepository(TriviaDataRepository triviaDataRepository) {
        return triviaDataRepository;
    }
}
