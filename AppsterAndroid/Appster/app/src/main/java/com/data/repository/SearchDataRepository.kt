package com.data.repository

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.apster.common.Constants
import com.data.entity.mapper.SearchUserMapper
import com.data.entity.requests.SearchUserRequestEntity
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.SearchUserDataSource
import com.domain.models.SearchUserModel
import com.domain.repository.SearchUserRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchDataRepository @Inject constructor(@Remote private val searchUserDataSource: SearchUserDataSource) : SearchUserRepository {
    private val searchUserMapper: SearchUserMapper by lazy { SearchUserMapper() }
    override fun searchUser(request: SearchUserRequestEntity): Observable<BaseDataPagingResponseModel<SearchUserModel>> {
        return searchUserDataSource.searchUser(request)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
                .map(searchUserMapper::transform)
    }

}