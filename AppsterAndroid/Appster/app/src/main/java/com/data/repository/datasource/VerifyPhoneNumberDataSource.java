package com.data.repository.datasource;

import com.appster.domain.PhoneVerification;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public interface VerifyPhoneNumberDataSource {
    Observable<BaseResponse<PhoneVerification>> verifyPhoneNumber(PhoneVerificationRequest request);
}