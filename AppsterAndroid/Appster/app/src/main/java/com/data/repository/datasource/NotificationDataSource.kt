package com.data.repository.datasource

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.appster.webservice.response.BaseResponse
import com.data.entity.NotificationListEntity
import rx.Observable

interface NotificationDataSource {

    fun getNotificationList (notificationStatus: Int, nextIndex:Int, pageLimit:Int): Observable<BaseResponse<BaseDataPagingResponseModel<NotificationListEntity>>>

}