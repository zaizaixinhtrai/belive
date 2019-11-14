package com.appster.features.login.phoneLogin.phoneSignInSignUp;

import com.appster.AppsterApplication;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.apster.common.Constants;
import com.data.repository.PhoneLoginRepository;
import com.data.repository.datasource.cloud.CloudVerifyPhoneNumberDataSource;
import com.domain.interactors.phoneLogin.VerifyPhoneNumberUseCase;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneSignInSignUpPresenter extends BasePresenter<PhoneSignInSignUpContract.View> implements PhoneSignInSignUpContract.UserActions {

    private VerifyPhoneNumberUseCase mPhoneVerificationUseCase;

    public PhoneSignInSignUpPresenter() {
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        AppsterWebserviceAPI service = AppsterWebServices.get();
        String auth = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
        CloudVerifyPhoneNumberDataSource dataSource = new CloudVerifyPhoneNumberDataSource(service, auth);
        PhoneLoginRepository repository = new PhoneLoginRepository(dataSource, null, null, null, null, null);
        mPhoneVerificationUseCase = new VerifyPhoneNumberUseCase(uiThread, ioThread, repository);
    }

    @Override
    public void verifyPhoneNumber(String countryCode, String phoneNumber) {
        getView().showProgress();
        PhoneVerificationRequest request = new PhoneVerificationRequest(countryCode, phoneNumber);
        addSubscription(mPhoneVerificationUseCase.execute(request)
                .subscribe(phoneVerificationBaseResponse -> {
                    if (phoneVerificationBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                        getView().onVerifyPhoneNumberSuccessfully(phoneVerificationBaseResponse.getData());
                    }else {
                        getView().onVerifyPhoneNumberFailed(phoneVerificationBaseResponse.getMessage());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }
}