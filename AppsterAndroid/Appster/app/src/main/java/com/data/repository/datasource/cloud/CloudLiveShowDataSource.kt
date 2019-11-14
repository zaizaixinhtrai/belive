package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.entity.LiveShowEntity
import com.data.entity.LiveShowStatusEntity
import com.data.entity.requests.LiveShowFriendNumberEntity
import com.data.repository.datasource.LiveShowDataSource
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 5/18/18.
 */
class CloudLiveShowDataSource @Inject constructor(private val service: AppsterWebserviceAPI, @Named(ApiServiceModule.APP_AUTHEN) private val authen: String) : LiveShowDataSource {


    override fun fetchLiveShow(): Observable<BaseResponse<List<LiveShowEntity>>> {
        return service.fetchLiveShows(authen)
    }

    override fun checkStatus(showId: Int): Observable<BaseResponse<LiveShowStatusEntity>> {
        return service.checkStatus(authen,showId)
    }

    override fun getFriendNumber(showId: Int): Observable<BaseResponse<LiveShowFriendNumberEntity>> {
        return  service.liveShowFriendNumber(authen,showId)
    }
}