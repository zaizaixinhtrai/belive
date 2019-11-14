package com.data.repository.datasource;

import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public interface RequestPhoneVerificationCodeDataSource {
    Observable<BaseResponse<Boolean>> requestVerificationCode(String otp);
}
