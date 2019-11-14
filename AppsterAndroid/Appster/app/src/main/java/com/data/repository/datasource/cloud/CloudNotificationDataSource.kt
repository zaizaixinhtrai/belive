package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseDataPagingResponseModel
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.entity.NotificationListEntity
import com.data.entity.requests.NotificationListRequestEntity
import com.data.repository.datasource.NotificationDataSource
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

class CloudNotificationDataSource @Inject constructor(private val service: AppsterWebserviceAPI, @Named(ApiServiceModule.APP_AUTHEN) private val authen: String) : NotificationDataSource {

    override fun getNotificationList(notificationStatus: Int, nextIndex:Int, pageLimit:Int): Observable<BaseResponse<BaseDataPagingResponseModel<NotificationListEntity>>> {
        return service.getNotificationList(authen, NotificationListRequestEntity(notificationStatus,nextIndex,pageLimit))
    }

}