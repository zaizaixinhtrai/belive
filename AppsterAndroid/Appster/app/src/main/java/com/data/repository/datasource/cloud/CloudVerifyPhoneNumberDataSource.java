package com.data.repository.datasource.cloud;

import com.appster.domain.PhoneVerification;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.VerifyPhoneNumberDataSource;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public class CloudVerifyPhoneNumberDataSource implements VerifyPhoneNumberDataSource {

    private final AppsterWebserviceAPI mService;
    private final String mAuth;

    public CloudVerifyPhoneNumberDataSource(AppsterWebserviceAPI service, String auth) {
        mService = service;
        mAuth = auth;
    }

    @Override
    public Observable<BaseResponse<PhoneVerification>> verifyPhoneNumber(PhoneVerificationRequest request) {
        return mService.verifyPhone(mAuth, request);
    }
}
