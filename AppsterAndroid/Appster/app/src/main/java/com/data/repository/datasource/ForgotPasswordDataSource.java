package com.data.repository.datasource;

import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;

import rx.Observable;

/**
 * Created by linh on 26/10/2017.
 */

public interface ForgotPasswordDataSource {
    Observable<BaseResponse<PhoneLoginForgotPasswordResponse>> forgotPassword(PhoneLoginForgotPasswordRequestModel request);
}
