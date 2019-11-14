package com.domain.interactors.searchUser

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.data.di.SchedulerModule
import com.data.entity.requests.SearchUserRequestEntity
import com.domain.interactors.UseCase
import com.domain.models.SearchUserModel
import com.domain.repository.SearchUserRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchUserUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val searchUserRepository: SearchUserRepository)
    : UseCase<BaseDataPagingResponseModel<SearchUserModel>, SearchUserRequestEntity>(uiThread, executorThread) {

    override fun buildObservable(params: SearchUserRequestEntity): Observable<BaseDataPagingResponseModel<SearchUserModel>> {
        return searchUserRepository.searchUser(params)
    }
}