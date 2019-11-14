package com.data.di

import com.data.repository.LiveShowDataRepository
import com.data.repository.Remote
import com.data.repository.datasource.LiveShowDataSource
import com.data.repository.datasource.cloud.CloudLiveShowDataSource
import com.domain.repository.LiveShowRepository
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 5/18/18.
 */
@Module
class LiveShowRepositoryModule{
    @Remote
    @Provides
    fun provideRemoteDataSource(liveShowDataSource: CloudLiveShowDataSource) : LiveShowDataSource = liveShowDataSource

    @Provides
    fun prodivdeLiveShowRepository(liveShowDataRepository: LiveShowDataRepository) : LiveShowRepository = liveShowDataRepository
}