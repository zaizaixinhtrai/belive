package com.domain.interactors.login


import com.appster.webservice.request_models.LoginFacebookRequestModel
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
public class FacebookLoginUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val loginRepository: UserLoginRepository)
    : UseCase<BaseResponse<LoginResponseModel>, LoginFacebookRequestModel>(uiThread, executorThread) {
    override fun buildObservable(params: LoginFacebookRequestModel): Observable<BaseResponse<LoginResponseModel>>
            = loginRepository.loginWithFacebook(params)
}