package com.data.di

import com.data.repository.ExploreDataRepository
import com.data.repository.Remote
import com.data.repository.datasource.ExploreDataSource
import com.data.repository.datasource.cloud.CloudExploreDataSource
import com.domain.repository.ExploreStreamRepository
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 6/13/18.
 */

@Module
class ExploreRepositoryModule {
    @Provides
    @Remote
    fun provideRemoteDataSource(cloudExploreDataSource: CloudExploreDataSource): ExploreDataSource = cloudExploreDataSource

    @Provides
    fun provideExploreRepository(exploreStreamRepository: ExploreDataRepository): ExploreStreamRepository = exploreStreamRepository
}