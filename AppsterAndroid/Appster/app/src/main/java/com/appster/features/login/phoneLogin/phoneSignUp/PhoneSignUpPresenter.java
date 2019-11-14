package com.appster.features.login.phoneLogin.phoneSignUp;

import com.appster.features.mvpbase.BasePresenter;
import com.appster.manager.ShowErrorManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.apster.common.Constants;
import com.data.repository.PhoneLoginRepository;
import com.data.repository.datasource.cloud.CloudPhoneVerifyVerificationCodeDataSource;
import com.data.repository.datasource.cloud.CloudRequestPhoneVerificationCodeDataSource;
import com.domain.interactors.phoneLogin.RequestPhoneVerificationCodeUseCase;
import com.domain.interactors.phoneLogin.VerifyVerificationCodeUseCase;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneSignUpPresenter extends BasePresenter<PhoneSignUpContract.View> implements PhoneSignUpContract.UserActions {
    private static final String SECRET_KEY = "dredbnhgjyrc76vs6dvvvsd709*d^%djjjd%ddd";
    private RequestPhoneVerificationCodeUseCase mRequestPhoneVerificationCodeUseCase;
    private VerifyVerificationCodeUseCase mVerifyPhoneVerificationUseCase;

    public PhoneSignUpPresenter() {
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        AppsterWebserviceAPI service = AppsterWebServices.get();
        CloudRequestPhoneVerificationCodeDataSource dataSource = new CloudRequestPhoneVerificationCodeDataSource(service, SECRET_KEY);
        CloudPhoneVerifyVerificationCodeDataSource dataSource1 = new CloudPhoneVerifyVerificationCodeDataSource(service);
        PhoneLoginRepository repository = new PhoneLoginRepository(null, dataSource, dataSource1, null, null, null);
        mRequestPhoneVerificationCodeUseCase = new RequestPhoneVerificationCodeUseCase(uiThread, ioThread, repository);
        mVerifyPhoneVerificationUseCase = new VerifyVerificationCodeUseCase(uiThread, ioThread, repository);
    }

    @Override
    public void requestVerificationCode(String otp) {
        getView().showProgress();
        addSubscription(mRequestPhoneVerificationCodeUseCase.execute(otp)
                .subscribe(booleanBaseResponse -> {
                    if (booleanBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                        getView().onRequestVerificationCodeSuccessfully(booleanBaseResponse.getData());

                    }else if (booleanBaseResponse.getCode() == ShowErrorManager.REQUEST_VERIFICATION_CODE_REACHED_LIMITED){
                        getView().onRequestVerificationReachedLimited(booleanBaseResponse.getMessage());

                    }else{
                        getView().onRequestVerificationCodeFailed(booleanBaseResponse.getMessage());
                    }
                    getView().hideProgress();
                }, e -> {
                    getView().hideProgress();
                    Timber.e(e);
                }));
    }

    @Override
    public void verifyVerificationCode(String otp, String phoneVerificationCode) {
        PhoneVerifyVerificationCodeRequest request = new PhoneVerifyVerificationCodeRequest(otp, phoneVerificationCode);
        addSubscription(mVerifyPhoneVerificationUseCase.execute(request)
                .subscribe(phoneVerificationBaseResponse -> {
                    if (phoneVerificationBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                        getView().onVerifyVerificationCodeSuccessfully();

                    }else if (phoneVerificationBaseResponse.getCode() == ShowErrorManager.VERIFICATION_CODE_INVALID){
                        getView().onVerifyVerificationCodeFailed(phoneVerificationBaseResponse.getMessage());

                    }else{
                        getView().onVerifyVerificationCodeFailed(phoneVerificationBaseResponse.getMessage());
                    }
                }, Timber::e));
    }
}
