package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.StreamsRecentResponse
import com.apster.common.Constants
import com.data.di.ApiServiceModule
import com.data.entity.requests.StreamsRecentRequestEntity
import com.data.repository.datasource.ExploreDataSource
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 6/13/18.
 */
class CloudExploreDataSource @Inject constructor(private val mService: AppsterWebserviceAPI, @Named(ApiServiceModule.APP_AUTHEN) private val mAuthen: String) : ExploreDataSource {

    override fun getStreams(pageId: Int): Observable<BaseResponse<StreamsRecentResponse>> {
        return mService.getStreamsRecent(mAuthen, StreamsRecentRequestEntity(pageId, Constants.PAGE_LIMITED))
    }
}