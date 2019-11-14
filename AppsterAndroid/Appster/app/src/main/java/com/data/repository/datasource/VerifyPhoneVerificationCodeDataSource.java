package com.data.repository.datasource;

import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public interface VerifyPhoneVerificationCodeDataSource {
    Observable<BaseResponse<Boolean>> verify(PhoneVerifyVerificationCodeRequest request);
}
