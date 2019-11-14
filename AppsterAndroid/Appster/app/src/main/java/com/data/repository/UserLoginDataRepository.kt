package com.data.repository

import com.appster.webservice.request_models.*
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel
import com.data.repository.datasource.LoginDataSource
import com.domain.repository.UserLoginRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created by thanhbc on 5/10/18.
 */
class UserLoginDataRepository @Inject constructor(@Remote private val loginDataSource: LoginDataSource): UserLoginRepository{

    override fun loginWithPhoneNumber(request: PhoneLoginRequestModel): Observable<BaseResponse<LoginResponseModel>> =  loginDataSource.loginWithPhoneNumber(request)

    override fun loginWithFacebook(request: LoginFacebookRequestModel): Observable<BaseResponse<LoginResponseModel>> = loginDataSource.loginWithFacebook(request)

    override fun loginWithGoogle(request: GoogleLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>  = loginDataSource.loginWithGoogle(request)
    override fun loginWithTwitter(request: TwitterLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>  = loginDataSource.loginWithTwitter(request)
    override fun loginWithInstagram(request: InstagramLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>  = loginDataSource.loginWithInstagram(request)
    override fun loginWithWeibo(request: WeiboLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>  = loginDataSource.loginWithWeibo(request)

}