package com.appster.features.login.phoneLogin.newPassword;

import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.apster.common.Constants;
import com.data.repository.PhoneLoginRepository;
import com.data.repository.datasource.cloud.CloudPhoneSignInResetPasswordDataSource;
import com.domain.interactors.phoneLogin.PhoneSignInResetPasswordUseCase;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneSignInNewPasswordPresenter extends BasePresenter<PhoneSignInNewPasswordContract.View> implements PhoneSignInNewPasswordContract.UserActions{

    private final PhoneSignInResetPasswordUseCase mPhoneSignInResetPasswordUseCase;

    public PhoneSignInNewPasswordPresenter() {
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        AppsterWebserviceAPI service = AppsterWebServices.get();
        CloudPhoneSignInResetPasswordDataSource cloudPhoneSignInResetPasswordDataSource = new CloudPhoneSignInResetPasswordDataSource(service);
        PhoneLoginRepository repository = new PhoneLoginRepository(null, null, null, null, null, cloudPhoneSignInResetPasswordDataSource);
        mPhoneSignInResetPasswordUseCase = new PhoneSignInResetPasswordUseCase(uiThread, ioThread, repository);
    }

    @Override
    public void resetPassword(PhoneLoginResetPasswordRequest request) {
        getView().showProgress();
        addSubscription(mPhoneSignInResetPasswordUseCase.execute(request)
                .subscribe(booleanBaseResponse -> {
                    if (booleanBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                        getView().onResetPasswordSuccessfully();
                    }else{
                        getView().onResetPasswordFailed(booleanBaseResponse.getMessage());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }
}
