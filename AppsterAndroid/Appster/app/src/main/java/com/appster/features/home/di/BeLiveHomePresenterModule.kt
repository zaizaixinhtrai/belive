package com.appster.features.home.di

import com.appster.features.home.BeLiveHomeContract
import com.appster.features.home.BeLiveHomePresenter
import com.appster.features.home.BeLiveHomeScreenFragment
import com.data.di.LiveShowRepositoryModule
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 5/18/18.
 */
@Module (includes = [LiveShowRepositoryModule::class])
class BeLiveHomePresenterModule{
    @Provides
    fun provideView(homeView: BeLiveHomeScreenFragment): BeLiveHomeContract.View = homeView


    @Provides
    fun providePresenter(presenter: BeLiveHomePresenter): BeLiveHomeContract.UserActions = presenter

}