package com.appster.features.notification.di

import com.appster.base.FragmentScope
import com.appster.features.notification.NotifyContract
import com.appster.features.notification.NotifyFragment
import com.appster.features.notification.NotifyPresenter
import com.data.repository.NotificationDataRepository
import com.data.repository.Remote
import com.data.repository.datasource.NotificationDataSource
import com.data.repository.datasource.cloud.CloudNotificationDataSource
import com.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class NotifyFragmentModule {
    @ContributesAndroidInjector(modules = [(NotifyPresenterModule::class)])
    @FragmentScope
    internal abstract fun provideNotifyFragmentFactory(): NotifyFragment
}