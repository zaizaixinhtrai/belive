package com.data.repository

import com.appster.models.NotificationItemModel
import com.appster.webservice.response.BaseDataPagingResponseModel
import com.apster.common.Constants
import com.data.entity.mapper.NotificationMapper
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.NotificationDataSource
import com.domain.repository.NotificationRepository
import rx.Observable
import javax.inject.Inject

class NotificationDataRepository @Inject constructor(@Remote private val liveShowDataSource: NotificationDataSource) : NotificationRepository {

    private val notificationMapper: NotificationMapper by lazy { NotificationMapper() }
    override fun getNotificationList(notificationStatus: Int, nextIndex: Int, pageLimit: Int): Observable<BaseDataPagingResponseModel<NotificationItemModel>> {
        return liveShowDataSource.getNotificationList(notificationStatus, nextIndex, pageLimit)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
                .map(notificationMapper::transform)
    }
}