package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import rx.Observable

/**
 * Created by Ngoc on 5/17/2018.
 */
interface MainDataSource{
    fun checkHasLiveVideo(): Observable<BaseResponse<Boolean>>
}