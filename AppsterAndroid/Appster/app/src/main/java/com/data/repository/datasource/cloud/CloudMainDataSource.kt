package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.repository.datasource.MainDataSource
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 5/17/2018.
 */
class CloudMainDataSource @Inject constructor(private val mService: AppsterWebserviceAPI, @Named(ApiServiceModule.APP_AUTHEN) private val mAuthen: String): MainDataSource {
    override fun checkHasLiveVideo(): Observable<BaseResponse<Boolean>> {
        return mService.checkHasLiveVideo(mAuthen)
    }

}