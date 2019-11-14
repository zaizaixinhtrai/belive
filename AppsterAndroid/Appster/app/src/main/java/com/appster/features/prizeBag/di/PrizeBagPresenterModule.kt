package com.appster.features.prizeBag.di

import com.appster.features.points.di.PointsModule
import com.appster.features.prizeBag.PrizeBagActivity
import com.appster.features.prizeBag.PrizeBagContract
import com.appster.features.prizeBag.PrizeBagPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [PointsModule::class])
class PrizeBagPresenterModule {
    @Provides
    fun provideView(prizeBagView: PrizeBagActivity): PrizeBagContract.PrizeBagView = prizeBagView

    @Provides
    fun providePresenter(presenter: PrizeBagPresenter): PrizeBagContract.PrizeBagActions = presenter
}