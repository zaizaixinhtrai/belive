package com.domain.interactors.phoneLogin;

import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 25/10/2017.
 */

public class VerifyVerificationCodeUseCase extends UseCase<BaseResponse<Boolean>, PhoneVerifyVerificationCodeRequest> {
    private final PhoneLoginRepository mRepository;
    public VerifyVerificationCodeUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(PhoneVerifyVerificationCodeRequest request) {
        return mRepository.verifyVerificationCde(request);
    }
}
