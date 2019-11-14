package com.data.repository.datasource;

import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 26/10/2017.
 */

public interface PhoneSignInResetPasswordDataSource {
    Observable<BaseResponse<Boolean>> resetPassword(PhoneLoginResetPasswordRequest request);
}
