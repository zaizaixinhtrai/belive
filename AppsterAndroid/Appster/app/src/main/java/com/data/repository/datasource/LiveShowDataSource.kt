package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import com.data.entity.LiveShowEntity
import com.data.entity.LiveShowStatusEntity
import com.data.entity.requests.LiveShowFriendNumberEntity
import rx.Observable

/**
 * Created by thanhbc on 5/18/18.
 */
interface LiveShowDataSource{
    fun fetchLiveShow() : Observable<BaseResponse<List<LiveShowEntity>>>
    fun checkStatus(showId: Int) : Observable<BaseResponse<LiveShowStatusEntity>>
    fun getFriendNumber(showId:Int):Observable<BaseResponse<LiveShowFriendNumberEntity>>
}