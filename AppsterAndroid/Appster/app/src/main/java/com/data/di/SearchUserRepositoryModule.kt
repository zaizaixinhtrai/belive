package com.data.di

import com.data.repository.Remote
import com.data.repository.SearchDataRepository
import com.data.repository.datasource.SearchUserDataSource
import com.data.repository.datasource.cloud.CloudSearchUserDataSource
import com.domain.repository.SearchUserRepository
import dagger.Module
import dagger.Provides

/**
 * Created by Ngoc on 5/28/2018.
 */
@Module
class SearchUserRepositoryModule {

    @Provides
    @Remote
    fun provideRemoteDataSource(cloudSearchUserDataSource: CloudSearchUserDataSource): SearchUserDataSource = cloudSearchUserDataSource

    @Provides
    fun provideTransactionRepository(transactionDataRepository: SearchDataRepository): SearchUserRepository = transactionDataRepository
}