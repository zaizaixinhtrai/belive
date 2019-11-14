package com.domain.interactors.login

import com.appster.webservice.request_models.TwitterLoginRequestModel
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.UserLoginRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named
/**
 * Created by thanhbc on 5/10/18.
 */
public class TwitterLoginUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val loginRepository: UserLoginRepository)
    : UseCase<BaseResponse<LoginResponseModel>, TwitterLoginRequestModel>(uiThread, executorThread) {
    override fun buildObservable(params: TwitterLoginRequestModel): Observable<BaseResponse<LoginResponseModel>> = loginRepository.loginWithTwitter(params)
}