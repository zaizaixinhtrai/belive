package com.appster.main

import com.appster.features.points.di.PointsModule
import com.data.di.MainRepositoryModule
import dagger.Module
import dagger.Provides

/**
 * Created by Ngoc on 5/17/2018.
 */
@Module(includes = [MainRepositoryModule::class, PointsModule::class])
class MainPresenterModule {
    @Provides
    fun provideView(mainActivity: MainActivity): MainContract.MainView = mainActivity

    @Provides
    fun providePresenter(mainPresenter: MainPresenter): MainContract.MainActions = mainPresenter

}