package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.RequestPhoneVerificationCodeDataSource;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public class CloudRequestPhoneVerificationCodeDataSource implements RequestPhoneVerificationCodeDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mSecretKey;

    public CloudRequestPhoneVerificationCodeDataSource(AppsterWebserviceAPI service, String secretKey) {
        mService = service;
        mSecretKey = secretKey;
    }

    @Override
    public Observable<BaseResponse<Boolean>> requestVerificationCode(String otp) {
        Map<String, String> otpMap = new HashMap<>();
        otpMap.put("OtpToken", otp);
        return mService.requestPhoneVerificationCode(mSecretKey, otpMap);
    }
}
