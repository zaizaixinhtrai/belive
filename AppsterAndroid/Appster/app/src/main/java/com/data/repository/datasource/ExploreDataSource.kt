package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.StreamsRecentResponse
import rx.Observable

/**
 * Created by thanhbc on 6/13/18.
 */
interface ExploreDataSource {
    fun getStreams(pageId: Int = 0) : Observable<BaseResponse<StreamsRecentResponse>>
}