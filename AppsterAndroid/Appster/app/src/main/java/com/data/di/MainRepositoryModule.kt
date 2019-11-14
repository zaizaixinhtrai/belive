package com.data.di

import com.data.repository.MainDataRepository
import com.data.repository.PointSystemDataRepository
import com.data.repository.Remote
import com.data.repository.datasource.MainDataSource
import com.data.repository.datasource.PointSystemDataSource
import com.data.repository.datasource.cloud.CloudMainDataSource
import com.data.repository.datasource.cloud.CloudPointSystemDataSource
import com.domain.repository.MainRepository
import com.domain.repository.PointSystemRepository
import dagger.Module
import dagger.Provides

/**
 * Created by Ngoc on 5/17/2018.
 */
@Module
class MainRepositoryModule{

    @Remote
    @Provides
    fun provideRemoteDataSource(mainDataSource: CloudMainDataSource) : MainDataSource = mainDataSource

    @Provides
    fun provideMainRepository(mainDataRepository: MainDataRepository) : MainRepository = mainDataRepository
    
}