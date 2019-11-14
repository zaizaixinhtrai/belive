package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;
import com.data.repository.datasource.ForgotPasswordDataSource;

import rx.Observable;

/**
 * Created by linh on 26/10/2017.
 */

public class CloudForgotPasswordDataSource implements ForgotPasswordDataSource {
    private final AppsterWebserviceAPI mService;

    public CloudForgotPasswordDataSource(AppsterWebserviceAPI service) {
        mService = service;
    }

    @Override
    public Observable<BaseResponse<PhoneLoginForgotPasswordResponse>> forgotPassword(PhoneLoginForgotPasswordRequestModel request) {
        return mService.forgotPassword(request);
    }
}
