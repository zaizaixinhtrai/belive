package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseDataPagingResponseModel
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.entity.SearchUserEntity
import com.data.entity.requests.SearchUserRequestEntity
import com.data.repository.datasource.SearchUserDataSource
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 5/28/2018.
 */
class CloudSearchUserDataSource @Inject constructor(private val mService: AppsterWebserviceAPI,
                                                    @Named(ApiServiceModule.APP_AUTHEN) private val mAuthen: String) : SearchUserDataSource {
    override fun searchUser(request: SearchUserRequestEntity): Observable<BaseResponse<BaseDataPagingResponseModel<SearchUserEntity>>> {
        return mService.searchUser(mAuthen, request)
    }

}