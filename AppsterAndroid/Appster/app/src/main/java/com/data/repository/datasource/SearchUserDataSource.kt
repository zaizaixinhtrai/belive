package com.data.repository.datasource

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.appster.webservice.response.BaseResponse
import com.data.entity.SearchUserEntity
import com.data.entity.requests.SearchUserRequestEntity
import rx.Observable

/**
 * Created by Ngoc on 5/28/2018.
 */
interface SearchUserDataSource {
    fun searchUser(request: SearchUserRequestEntity): Observable<BaseResponse<BaseDataPagingResponseModel<SearchUserEntity>>>
}