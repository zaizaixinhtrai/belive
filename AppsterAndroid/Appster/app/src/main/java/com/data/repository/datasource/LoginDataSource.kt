package com.data.repository.datasource

import com.appster.webservice.request_models.*
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel

import rx.Observable

/**
 * Created by linh on 26/10/2017.
 */

interface LoginDataSource {
    fun loginWithPhoneNumber(request: PhoneLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>
    fun loginWithFacebook(request: LoginFacebookRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithGoogle(request: GoogleLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithTwitter(request: TwitterLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithInstagram(request: InstagramLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithWeibo(request: WeiboLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
}
