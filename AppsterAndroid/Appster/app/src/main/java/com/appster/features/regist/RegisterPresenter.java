package com.appster.features.regist;

import com.appster.manager.ShowErrorManager;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.CrashlyticsUtil;
import com.appster.utility.OneSignalUtil;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.RegisterWithFacebookRequestModel;
import com.appster.webservice.request_models.RegisterWithGoogleRequestModel;
import com.appster.webservice.request_models.RegisterWithInstagramRequestModel;
import com.appster.webservice.request_models.RegisterWithPhoneNumberRequestModel;
import com.appster.webservice.request_models.RegisterWithTwitterRequestModel;
import com.appster.webservice.request_models.RegisterWithWeChatRequestModel;
import com.appster.webservice.request_models.RegisterWithWeiboRequestModel;
import com.appster.webservice.request_models.VerifyUsernameRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.RegisterWithFacebookResponseModel;
import com.apster.common.Constants;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;

/**
 * Created by ThanhBan on 11/14/2016.
 */

public class RegisterPresenter implements RegisterContract.UserActions {

    private RegisterContract.RegisterView mView;
    private AppsterWebserviceAPI mService;
    private CompositeSubscription mCompositeSubscription;
    private String mAuthen;

    public RegisterPresenter(RegisterContract.RegisterView pView, AppsterWebserviceAPI pService) {
        attachView(pView);
        this.mService = pService;
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mAuthen = "Bearer " + mAppPreferences.getUserToken();
    }

    @Override
    public void checkUserIdAvailable(String userId) {
        VerifyUsernameRequestModel request = new VerifyUsernameRequestModel();
        request.setUserName(userId);
        mCompositeSubscription.add(mService.verifyUsername(mAuthen, request)
                .filter(verifyUsernameDataResponse -> verifyUsernameDataResponse != null && mView!=null)
                .subscribe(verifyUsernameDataResponse -> {
                    if (verifyUsernameDataResponse.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mView.loadError(verifyUsernameDataResponse.getMessage(), verifyUsernameDataResponse.getCode());
                    } else {
                        final boolean isValidUsername = verifyUsernameDataResponse.getData();
                        if (isValidUsername) {
                            mView.userIdAvailable();
                        } else {
                            mView.userIdInAvailable();
                        }
//                        mView.enableBeginButton(true);
                    }
                }, error -> {
                    if (mView != null) {
                        mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                    }
                    Timber.d(error.getMessage());
                }));
    }

    @Override
    public void getUserIdSuggestion(String expectedUserId) {
        mCompositeSubscription.add(mService.getSuggestedUserId(mAuthen, expectedUserId)
        .subscribe(baseResponse -> mView.onGetUserIdSuggestionSuccessfully(baseResponse.getData()), Timber::e));
    }

    @Override
    public void getUserIdSuggestion() {
        mCompositeSubscription.add(mService.getSuggestedUserId(mAuthen)
                .subscribe(baseResponse -> mView.onGetUserIdSuggestionSuccessfully(baseResponse.getData()), Timber::e));
    }


    @Override
    public void register(RegisterWithGoogleRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithGoogle(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    mView.hideProgress();
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithFacebookRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithFacebook(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    mView.hideProgress();
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithInstagramRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithInstagram(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    mView.hideProgress();
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithTwitterRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithTwitter(requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithWeChatRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithWeChat(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithWeiboRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithWeibo(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void register(RegisterWithPhoneNumberRequestModel requestModel) {
        mView.showProgress();
        mCompositeSubscription.add(mService.registerWithPhoneNumber(mAuthen, requestModel.build())
                .subscribe(this::onCreateProfileResponse, throwable -> {
                    Timber.d(throwable.getMessage());
                    mView.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void attachView(RegisterContract.RegisterView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    private void onCreateProfileResponse(BaseResponse<RegisterWithFacebookResponseModel> registerResponse){
        mView.hideProgress();
        if (registerResponse == null) return;
        if (registerResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

            // update crashlytics user data
            CrashlyticsUtil.setUser(registerResponse.getData().getUserInfo());
            // update amplitude user data
            EventTracker.setUser(registerResponse.getData().getUserInfo());
            // OneSignal
            OneSignalUtil.setUser(registerResponse.getData().getUserInfo());

            mView.onUserRegisterCompleted(registerResponse.getData().getUserInfo(),
                    registerResponse.getData().getAccess_token());

        } else if (registerResponse.getCode() == ShowErrorManager.ADMIN_BLOCKED){
            mView.onAdminBlocked(registerResponse.getMessage());
            EventTracker.trackEvent(EventTrackingName.EVENT_REGISTER_FAIL);

        }else {
            mView.loadError(registerResponse.getMessage(), registerResponse.getCode());
            EventTracker.trackEvent(EventTrackingName.EVENT_REGISTER_FAIL);
        }
    }
}
