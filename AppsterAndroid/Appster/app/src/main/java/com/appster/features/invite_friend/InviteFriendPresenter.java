package com.appster.features.invite_friend;

import com.appster.AppsterApplication;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.tracking.EventTracker;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.UserProfileRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.exceptions.BeLiveServerException;
import com.domain.interactors.inviteFriend.EditReferralCodeUseCase;
import com.domain.models.EditReferralModel;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Ngoc on 5/16/2017.
 */

public class InviteFriendPresenter extends BasePresenter<InviteFriendContract.InviteFriendView> implements InviteFriendContract.UserActions {
    private EditReferralCodeUseCase mEditReferralCodeUseCase = null;

    @Inject
    public InviteFriendPresenter(InviteFriendContract.InviteFriendView view, EditReferralCodeUseCase editReferralCodeUseCase) {
        attachView(view);
        mEditReferralCodeUseCase = editReferralCodeUseCase;
    }

    @Override
    public void updateRefId(String refId) {
        getView().showProgress();
        addSubscription(mEditReferralCodeUseCase.execute(EditReferralCodeUseCase.Params.byId(Integer.parseInt(refId)))
                .subscribe(referalModel -> {
                    getView().hideProgress();
                    if (referalModel == null) return;
                    AppsterApplication.mAppPreferences.getUserModel().setRefId(refId);
                    getView().onUpdateLayoutCompleted();
                    trackingReferralCode(referalModel);

                }, error -> {
                    int errorCode = Constants.RETROFIT_ERROR;
                    if (error instanceof BeLiveServerException) {
                        BeLiveServerException serverException = (BeLiveServerException) error;
                        if (serverException.code == 1030) {
                            getView().errorHasRefId(serverException.getMessage());
                            getUserInfo();
                        }
                        errorCode = serverException.code;
                    }
                    getView().hideProgress();
                    Timber.e(error.getMessage());
                    getView().loadError(error.getMessage(), errorCode);
                }));
    }

    @Override
    public void getUserInfo() {
        final UserProfileRequestModel request = new UserProfileRequestModel();
        request.setUser_id(AppsterApplication.mAppPreferences.getUserModel().getUserId());
        addSubscription(AppsterWebServices.get().getUserProfile("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(userProfileResponseModel -> {

                    if (userProfileResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        AppsterApplication.mAppPreferences.saveUserInforModel(userProfileResponseModel.getData().getUser());
                    }

                }, error -> {
                    Timber.e(error);
                }));
    }

    @Override
    public void getAppConfigFromServer() {
        addSubscription(AppsterWebServices.get().getAppConfigs(AppsterUtility.getAuth())
                .filter(appConfigModelBaseResponse -> appConfigModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && appConfigModelBaseResponse.getData() != null)
                .map(BaseResponse::getData)
                .subscribe(appConfig -> getView().onGetAppConfigSuccessfully(appConfig), Timber::e));
    }

    @Override
    public void trackingReferralCode(EditReferralModel model) {
        EventTracker.trackingReferralCode(model.getRequesterUserId(), model.getReceiverUserId(),
                model.isTriviaRequester(), model.isTriviaReceiver());
    }
}
