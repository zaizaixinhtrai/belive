package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseResponse
import com.data.entity.GiftStoreEntity
import com.data.repository.datasource.GiftStoreDataSource
import rx.Observable

/**
 * Created by thanhbc on 3/27/18.
 */
class CloudGiftStoreDataSource(val service: AppsterWebserviceAPI, val authen: String = "") : GiftStoreDataSource {
    override fun fetchGiftStore(): Observable<BaseResponse<GiftStoreEntity>> {
        return service.getGiftStore(authen)
    }
}