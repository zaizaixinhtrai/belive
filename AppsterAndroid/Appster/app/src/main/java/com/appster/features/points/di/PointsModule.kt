package com.appster.features.points.di

import com.data.repository.PointSystemDataRepository
import com.data.repository.datasource.PointSystemDataSource
import com.data.repository.datasource.cloud.CloudPointSystemDataSource
import com.domain.repository.PointSystemRepository
import dagger.Module
import dagger.Provides

/**
 *  Created by DatTN on 10/23/2018
 */
@Module
class PointsModule {

    @Provides
    fun provideDataSource(dataSource: CloudPointSystemDataSource): PointSystemDataSource = dataSource

    @Provides
    fun provideRepo(repo: PointSystemDataRepository): PointSystemRepository = repo

}