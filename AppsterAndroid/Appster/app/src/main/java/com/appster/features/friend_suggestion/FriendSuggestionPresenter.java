package com.appster.features.friend_suggestion;

import android.util.ArrayMap;

import com.appster.AppsterApplication;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.utility.SpannableUtil;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.FollowAllUsersResquestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.apster.common.Constants;

import java.util.ArrayList;
import java.util.Map;

import timber.log.Timber;

public class FriendSuggestionPresenter extends BasePresenter<FriendSuggestionContract.View> implements FriendSuggestionContract.UserActions {

    private final AppsterWebserviceAPI mService;
    private String mAuth;
    private int mFriendListOnBeliveOffset;
    private boolean isEndFriendListOnBelive;

    public FriendSuggestionPresenter(AppsterWebserviceAPI service) {
        this.mService = service;
        mAuth = AppsterApplication.mAppPreferences.getUserTokenRequest();
        isEndFriendListOnBelive = false;
        mFriendListOnBeliveOffset = 0;
    }

    @Override
    public void setIsEndFriendListOnBelive(boolean endFriendListOnBelive) {
        isEndFriendListOnBelive = endFriendListOnBelive;
    }

    @Override
    public boolean getFriendListOnBelive(String token, boolean isRefresh) {
        if (isEndFriendListOnBelive) {
            getView().onGetFriendListOnBeliveSucessError();
            return false;
        }
        Map<String, String> params = new ArrayMap<>();
        params.put("token", token);
        params.put("limit", String.valueOf(Constants.PAGE_LIMITED));
        params.put("nextid", String.valueOf(mFriendListOnBeliveOffset));
        addSubscription(mService.getFriendListOnBelive(mAuth, params)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        BaseDataPagingResponseModel<FriendSuggestionModel> baseDataPagingResponse = baseResponse.getData();
                        if (baseDataPagingResponse != null) {
                            mFriendListOnBeliveOffset = baseDataPagingResponse.getNextId();
                            isEndFriendListOnBelive = baseDataPagingResponse.isEnd();
                            getView().onGetFriendListOnBeliveSucessfully(baseDataPagingResponse.getResult(), isRefresh);
                        } else {
                            isEndFriendListOnBelive = true;
                            getView().onGetFriendListOnBeliveSucessfully(new ArrayList<>(), isRefresh);
                        }
                    }
                }, Timber::e));
        return true;
    }

    @Override
    public void getSuggestedFriend() {
        getView().showProgress();
        addSubscription(mService.getSuggestion(mAuth, 2)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onGetSuggestedFriendSuccessfully(baseResponse.getData());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }

    @Override
    public void getAppConfigFromServer() {
        addSubscription(AppsterWebServices.get().getAppConfigs(mAuth)
                .filter(appConfigModelBaseResponse -> appConfigModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && appConfigModelBaseResponse.getData() != null)
                .map(baseResponse -> SpannableUtil.replaceGemIcon(getView().getViewContext(), baseResponse.getData().rewardMsgFriendSuggestion))
                .subscribe(appConfig -> getView().onGetAppConfigSuccessfully(appConfig), Timber::e));
    }

    @Override
    public void followAllUsers(String userIds, boolean isFirstCall) {
        if (!isFirstCall) {
            getView().showProgress();
        }
        addSubscription(mService.followAllUsers(mAuth, new FollowAllUsersResquestModel(userIds))
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
//                        if (!isFirstCall) {
//                            getView().onFollowAllUsersSuccess();
//                        }
                        getView().onFollowAllUsersSuccess();
                    } else {
//                        if (!isFirstCall) {
//                            getView().onFollowAllUsersFail(baseResponse.getMessage(), baseResponse.getCode());
//                        }
                        getView().onFollowAllUsersFail(baseResponse.getMessage(), baseResponse.getCode());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }

    @Override
    public void unFollowAllUsers(String userIds) {
        getView().showProgress();
        addSubscription(mService.unfollowAllUsers(mAuth, new FollowAllUsersResquestModel(userIds))
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onUnfollowAllUsersSuccess();
                    } else {
                        getView().onUnfollowAllUsersFail(baseResponse.getMessage(), baseResponse.getCode());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
    }
}
