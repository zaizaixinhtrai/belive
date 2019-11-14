package com.appster.features.blocked_screen;

import com.appster.AppsterApplication;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.BasePagingRequestModel;
import com.appster.webservice.request_models.UnblockUserRequestModel;
import com.appster.domain.BlockedUserModel;
import com.apster.common.Constants;
import com.apster.common.LogUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by linh on 27/12/2016.
 */

public class BlockedUserPresenter implements BlockedScreenContract.UserActions {
    private AppsterWebserviceAPI mService;
    private BlockedScreenContract.BlockedUserView view;
    private CompositeSubscription compositeSubscription;
    private String auth;
    private int offset = 0;
    boolean isEndList = false;

    public BlockedUserPresenter(BlockedScreenContract.BlockedUserView view, AppsterWebserviceAPI service) {
        attachView(view);
        this.mService = service;
        auth = AppsterApplication.mAppPreferences.getUserTokenRequest();
        this.compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    //===========
    @Override
    public void attachView(BlockedScreenContract.BlockedUserView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    void getBlockedUsers() {
        if (isEndList) {
            return;
        }
        view.showProgress();
        BasePagingRequestModel requestModel = new BasePagingRequestModel();
        requestModel.setNextId(offset);
        compositeSubscription.add(AppsterWebServices.get().getBlockedUsers(auth, requestModel)
                .subscribe(blockedUserResponse -> {
                    view.hideProgress();
                    if (blockedUserResponse == null) {
                        view.loadError("No data response!!", 9999);
                    } else {
                        if (blockedUserResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            view.onBlockedListResponse(blockedUserResponse.getData().getResult());
                            offset = blockedUserResponse.getData().getNextId();
                            isEndList = blockedUserResponse.getData().isEnd();
                        }
                    }
                }, throwable -> {
                    view.hideProgress();
                    view.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    void unblockUser(BlockedUserModel usersItemModel, int position) {
        UnblockUserRequestModel request = new UnblockUserRequestModel();
        request.setUnBlockUserId(usersItemModel.getUserId());

        compositeSubscription.add(AppsterWebServices.get().unblockUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(reportUserResponseModel -> {
                    if (reportUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        view.onUnblockUserSuccessfully(position);
                    } else {
                        view.loadError(reportUserResponseModel.getMessage(), Constants.RETROFIT_ERROR);
                        LogUtils.logE("onblockuser", reportUserResponseModel.getMessage());
                    }
                }, throwable -> view.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR)));
    }
}
