package com.domain.repository

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.data.entity.requests.SearchUserRequestEntity
import com.domain.models.SearchUserModel
import rx.Observable

/**
 * Created by Ngoc on 5/28/2018.
 */
interface SearchUserRepository{
    fun searchUser(request: SearchUserRequestEntity): Observable<BaseDataPagingResponseModel<SearchUserModel>>
}