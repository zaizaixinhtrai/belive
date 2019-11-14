package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.PhoneSignInResetPasswordDataSource;

import rx.Observable;

/**
 * Created by linh on 26/10/2017.
 */

public class CloudPhoneSignInResetPasswordDataSource implements PhoneSignInResetPasswordDataSource {

    private final AppsterWebserviceAPI mService;

    public CloudPhoneSignInResetPasswordDataSource(AppsterWebserviceAPI service) {
        mService = service;
    }

    @Override
    public Observable<BaseResponse<Boolean>> resetPassword(PhoneLoginResetPasswordRequest request) {
        return mService.resetPassword(request);
    }
}
