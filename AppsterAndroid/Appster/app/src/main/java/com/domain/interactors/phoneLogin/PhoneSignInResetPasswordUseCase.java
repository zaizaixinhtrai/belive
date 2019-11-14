package com.domain.interactors.phoneLogin;

import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneSignInResetPasswordUseCase extends UseCase<BaseResponse<Boolean>, PhoneLoginResetPasswordRequest> {

    private final PhoneLoginRepository mRepository;

    public PhoneSignInResetPasswordUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(PhoneLoginResetPasswordRequest request) {
        return mRepository.resetPassword(request);
    }
}
