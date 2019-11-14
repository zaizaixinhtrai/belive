package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import com.data.entity.GiftStoreEntity
import rx.Observable

/**
 * Created by thanhbc on 3/27/18.
 */
interface GiftStoreDataSource{
    fun fetchGiftStore() : Observable<BaseResponse<GiftStoreEntity>>
}