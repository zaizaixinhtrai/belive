package com.domain.repository

import com.appster.models.NotificationItemModel
import com.appster.webservice.response.BaseDataPagingResponseModel
import rx.Observable

interface NotificationRepository {
    fun getNotificationList(notificationStatus: Int, nextIndex: Int, pageLimit:Int): Observable<BaseDataPagingResponseModel<NotificationItemModel>>
}