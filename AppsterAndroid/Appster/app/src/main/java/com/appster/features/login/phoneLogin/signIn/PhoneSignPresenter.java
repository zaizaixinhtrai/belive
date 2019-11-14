package com.appster.features.login.phoneLogin.signIn;

import com.appster.features.login.LoginHelper;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.manager.ShowErrorManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.apster.common.Constants;
import com.data.repository.PhoneLoginRepository;
import com.data.repository.datasource.cloud.CloudForgotPasswordDataSource;
import com.data.repository.datasource.cloud.CloudLoginDataSource;
import com.domain.interactors.phoneLogin.PhoneSignInForgotUseCase;
import com.domain.interactors.phoneLogin.PhoneSignInUseCase;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 26/10/2017.
 */

public class PhoneSignPresenter extends BasePresenter<PhoneSignInContract.View> implements PhoneSignInContract.UserActions {
    private final PhoneSignInUseCase mPhoneLoginUseCase;
    private final PhoneSignInForgotUseCase mPhoneSignInForgotUseCase;

    public PhoneSignPresenter() {
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        AppsterWebserviceAPI service = AppsterWebServices.get();
        CloudLoginDataSource dataSource = new CloudLoginDataSource(service);
        CloudForgotPasswordDataSource cloudForgotPasswordDataSource = new CloudForgotPasswordDataSource(service);
        PhoneLoginRepository repository = new PhoneLoginRepository(null, null, null, dataSource, cloudForgotPasswordDataSource, null);
        mPhoneLoginUseCase = new PhoneSignInUseCase(uiThread, ioThread, repository);
        mPhoneSignInForgotUseCase = new PhoneSignInForgotUseCase(uiThread, ioThread, repository);
    }

    @Override
    public void loginAppsterServerWithPhoneNumber(PhoneLoginRequestModel request) {
        getView().showProgress();
        addSubscription(mPhoneLoginUseCase.execute(request)
                .subscribe(loginResponse -> {
                    if (loginResponse.getCode() == ShowErrorManager.account_deactivated_or_suspended) {
                        getView().onAccountSuspended();
                        getView().hideProgress();
                        return;

                    } else if (loginResponse.getCode() == ShowErrorManager.ADMIN_BLOCKED) {
                        getView().onAdminBlocked(loginResponse.getMessage());
                        getView().hideProgress();
                        return;

                    }else if (loginResponse.getCode() == ShowErrorManager.SIGN_IN_INVALID_PASSWORD){
                        getView().onPasswordInvalid();
                        getView().hideProgress();
                        return;
                    }

                    if (loginResponse.getData() == null || loginResponse.getCode() == ShowErrorManager.user_not_found) {
                        getView().onUserNotFound(loginResponse.getMessage());
                        getView().hideProgress();
                        return;
                    }

                    addSubscription(LoginHelper.INSTANCE.updateUserProfile(getView().getViewContext(), loginResponse.getData())
                            .subscribeOn(rx.schedulers.Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(o -> {
                                getView().onLoginSuccessfully();
                                getView().hideProgress();
                            }, Timber::e));

                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }

    @Override
    public void requestVerificationCode(PhoneLoginForgotPasswordRequestModel request) {
        getView().showProgress();
        addSubscription(mPhoneSignInForgotUseCase.execute(request)
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
}
