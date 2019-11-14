package com.appster.features.prizeBag.di

import com.appster.features.points.di.PointsModule
import com.appster.features.prizeBag.redemption.RedemptionActivity
import com.appster.features.prizeBag.redemption.RedemptionContract
import com.appster.features.prizeBag.redemption.RedemptionPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [PointsModule::class])
class RedemptionPresenterModule {

    @Provides
    fun provideView(view: RedemptionActivity): RedemptionContract.RedemptionView = view

    @Provides
    fun providePresenter(presenter: RedemptionPresenter): RedemptionContract.RedemptionActions = presenter
}