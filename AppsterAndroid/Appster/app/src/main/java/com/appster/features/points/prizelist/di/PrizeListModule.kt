package com.appster.features.points.prizelist.di

import com.appster.base.ActivityScope
import com.appster.features.points.prizelist.PrizeListContract
import com.appster.features.points.prizelist.PrizeListPresenter
import com.data.repository.PointSystemDataRepository
import com.data.repository.datasource.PointSystemDataSource
import com.data.repository.datasource.cloud.CloudPointSystemDataSource
import com.domain.interactors.pointsystem.GetPrizeListUseCase
import com.domain.repository.PointSystemRepository
import dagger.Module
import dagger.Provides

/**
 *  Created by DatTN on 10/29/2018
 */
@Module
class PrizeListModule {

    @Provides
    @ActivityScope
    fun provideDataSource(dataSource: CloudPointSystemDataSource): PointSystemDataSource = dataSource

    @Provides
    @ActivityScope
    fun provideRepo(repo: PointSystemDataRepository): PointSystemRepository = repo

    @Provides
    @ActivityScope
    fun providePrizeListPresenter(prizesUseCase: GetPrizeListUseCase): PrizeListContract.UserActions = PrizeListPresenter(prizesUseCase)

}