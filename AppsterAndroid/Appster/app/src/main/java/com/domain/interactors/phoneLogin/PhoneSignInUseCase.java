package com.domain.interactors.phoneLogin;

import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LoginResponseModel;
import com.data.repository.PhoneLoginRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneSignInUseCase extends UseCase<BaseResponse<LoginResponseModel>, PhoneLoginRequestModel> {
    private final PhoneLoginRepository mRepository;

    public PhoneSignInUseCase(Scheduler uiThread, Scheduler executorThread, PhoneLoginRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<LoginResponseModel>> buildObservable(PhoneLoginRequestModel requestModel) {
        return mRepository.loginWithPhoneNumber(requestModel);
    }
}
