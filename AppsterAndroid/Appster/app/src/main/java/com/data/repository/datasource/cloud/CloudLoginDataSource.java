package com.data.repository.datasource.cloud;

import androidx.annotation.NonNull;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.GoogleLoginRequestModel;
import com.appster.webservice.request_models.InstagramLoginRequestModel;
import com.appster.webservice.request_models.LoginFacebookRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.request_models.TwitterLoginRequestModel;
import com.appster.webservice.request_models.WeiboLoginRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LoginResponseModel;
import com.data.repository.datasource.LoginDataSource;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linh on 26/10/2017.
 */

public class CloudLoginDataSource implements LoginDataSource {
    final AppsterWebserviceAPI mService;
    @Inject
    public CloudLoginDataSource(AppsterWebserviceAPI service) {
        this.mService = service;
    }

    @NonNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithPhoneNumber(@NonNull PhoneLoginRequestModel request) {
        return mService.loginWithPhoneNumber(request);
    }

    @NotNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithFacebook(@NotNull LoginFacebookRequestModel request) {
        return mService.loginWithFacebook(request);
    }

    @NotNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithGoogle(@NotNull GoogleLoginRequestModel request) {
        return mService.loginWithGoogle(request,"close");
    }

    @NotNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithTwitter(@NotNull TwitterLoginRequestModel request) {
        return mService.loginTwitter(request);
    }

    @NotNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithInstagram(@NotNull InstagramLoginRequestModel request) {
        return mService.loginWithInstagram(request,"close");
    }

    @NotNull
    @Override
    public Observable<BaseResponse<LoginResponseModel>> loginWithWeibo(@NotNull WeiboLoginRequestModel request) {
        return mService.loginWithWeibo(request);
    }
}
