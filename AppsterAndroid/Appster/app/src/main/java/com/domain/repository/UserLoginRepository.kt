package com.domain.repository

import com.appster.webservice.request_models.*
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel
import rx.Observable

/**
 * Created by thanhbc on 5/10/18.
 */
interface UserLoginRepository{
    fun loginWithPhoneNumber(request: PhoneLoginRequestModel): Observable<BaseResponse<LoginResponseModel>>
    fun loginWithFacebook(request: LoginFacebookRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithGoogle(request: GoogleLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithTwitter(request: TwitterLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithInstagram(request: InstagramLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
    fun loginWithWeibo(request: WeiboLoginRequestModel) : Observable<BaseResponse<LoginResponseModel>>
}
