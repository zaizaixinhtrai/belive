package com.appster.features.notification.di

import com.data.repository.NotificationDataRepository
import com.data.repository.Remote
import com.data.repository.datasource.NotificationDataSource
import com.data.repository.datasource.cloud.CloudNotificationDataSource
import com.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides

@Module
class NotificationRepositoryModule {
    @Remote
    @Provides
    fun provideRemoteDataSource(notificationDataSource: CloudNotificationDataSource) : NotificationDataSource = notificationDataSource

    @Provides
    fun provideNotificationRepository(notificationDataRepository: NotificationDataRepository) : NotificationRepository = notificationDataRepository
}