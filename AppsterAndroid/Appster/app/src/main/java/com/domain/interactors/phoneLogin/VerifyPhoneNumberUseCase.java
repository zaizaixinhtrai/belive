package com.domain.interactors.phoneLogin;

import com.appster.domain.PhoneVerification;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 25/10/2017.
 */

public class VerifyPhoneNumberUseCase extends UseCase<BaseResponse<PhoneVerification>, PhoneVerificationRequest> {

    private final PhoneLoginRepository mRepository;

    public VerifyPhoneNumberUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<PhoneVerification>> buildObservable(PhoneVerificationRequest request) {
        return mRepository.verifyPhoneNumber(request);
    }
}
