package com.appster.features.notification.di

import com.appster.features.notification.NotifyContract
import com.appster.features.notification.NotifyFragment
import com.appster.features.notification.NotifyPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [NotificationRepositoryModule::class])
class NotifyPresenterModule {
    @Provides
    fun provideView(view: NotifyFragment): NotifyContract.View = view


    @Provides
    fun providePresenter(presenter: NotifyPresenter): NotifyContract.UserActions = presenter
}