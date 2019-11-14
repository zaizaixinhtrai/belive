package com.domain.interactors.phoneLogin;

import com.appster.webservice.response.BaseResponse;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 25/10/2017.
 */

public class RequestPhoneVerificationCodeUseCase extends UseCase<BaseResponse<Boolean>, String> {

    private final PhoneLoginRepository mRepository;

    public RequestPhoneVerificationCodeUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(String s) {
        return mRepository.requestVerificationCode(s);
    }
}
