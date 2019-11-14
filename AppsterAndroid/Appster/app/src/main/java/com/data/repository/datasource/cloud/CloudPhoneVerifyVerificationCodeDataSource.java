package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.VerifyPhoneVerificationCodeDataSource;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public class CloudPhoneVerifyVerificationCodeDataSource implements VerifyPhoneVerificationCodeDataSource {
    private final AppsterWebserviceAPI mService;

    public CloudPhoneVerifyVerificationCodeDataSource(AppsterWebserviceAPI service) {
        mService = service;
    }

    @Override
    public Observable<BaseResponse<Boolean>> verify(PhoneVerifyVerificationCodeRequest request) {
        return mService.verifyPhoneVerificationCode(request);
    }
}
