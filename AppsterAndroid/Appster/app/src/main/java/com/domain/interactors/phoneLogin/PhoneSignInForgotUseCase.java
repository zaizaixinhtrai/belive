package com.domain.interactors.phoneLogin;

import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneSignInForgotUseCase extends UseCase<BaseResponse<PhoneLoginForgotPasswordResponse>, PhoneLoginForgotPasswordRequestModel> {

    private final PhoneLoginRepository mRepository;

    public PhoneSignInForgotUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<PhoneLoginForgotPasswordResponse>> buildObservable(PhoneLoginForgotPasswordRequestModel requestModel) {
        return mRepository.forgotPassword(requestModel);
    }
}
