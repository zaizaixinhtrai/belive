package com.appster.features.fb_friends;

import android.util.ArrayMap;

import com.appster.AppsterApplication;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.SetFollowUserRequestModel;
import com.appster.webservice.request_models.SetUnfollowUserRequestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.apster.common.Constants;

import java.util.ArrayList;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by linh on 20/09/2017.
 */

public class FbFriendSuggestionPresenter extends BasePresenter<FbFriendSuggestionContract.FbFriendSuggestionView> implements FbFriendSuggestionContract.UserActions {

    private final AppsterWebserviceAPI mService;
    private String mAuth;
    private int mFriendListOnBeliveOffset;
    private boolean isEndFriendListOnBelive;

    public FbFriendSuggestionPresenter(FbFriendSuggestionContract.FbFriendSuggestionView view, AppsterWebserviceAPI service) {
        this.mService = service;
        mAuth =  AppsterApplication.mAppPreferences.getUserTokenRequest();
        isEndFriendListOnBelive = false;
        mFriendListOnBeliveOffset = 0;
        attachView(view);
    }

    @Override
    public void reset(){
        isEndFriendListOnBelive = false;
        mFriendListOnBeliveOffset = 0;
    }

    @Override
    public boolean getFriendListOnBelive(String token) {
        if (isEndFriendListOnBelive){
            getView().onGetFriendListOnBeliveError();
            return false;
        }
        if (mFriendListOnBeliveOffset == 0) {
            getView().showProgress();
        }
        Map<String, String> params = new ArrayMap<>();
        params.put("token", token);
        params.put("limit", String.valueOf(Constants.PAGE_LIMITED));
        params.put("nextid", String.valueOf(mFriendListOnBeliveOffset));
        addSubscription(mService.getFriendListOnBelive(mAuth, params)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                        BaseDataPagingResponseModel<FriendSuggestionModel> baseDataPagingResponse = baseResponse.getData();
                        if (baseDataPagingResponse != null){
                            mFriendListOnBeliveOffset = baseDataPagingResponse.getNextId();
                            isEndFriendListOnBelive = baseDataPagingResponse.isEnd();
                            getView().onGetFriendListOnBeliveSucessfully(baseDataPagingResponse.getResult());
                        }else{
                            isEndFriendListOnBelive = true;
                            getView().onGetFriendListOnBeliveSucessfully(new ArrayList<>());
                        }
                    }else{
                        getView().onGetFriendListOnBeliveSucessfully(new ArrayList<>());
                    }
                    getView().hideProgress();
                }, e -> {
                    Timber.e(e);
                    getView().hideProgress();
                }));
        return true;
    }

    @Override
    public void followUser(String userId) {
        SetFollowUserRequestModel request = new SetFollowUserRequestModel();
        request.setFollow_user_id(userId);
        addSubscription(AppsterWebServices.get().setFollowUser(mAuth, request)
                .subscribe(setFollowUserResponseModel -> {
                    if (setFollowUserResponseModel == null) {
                        return;
                    }
                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onChangeFollowStatusSuccessfully(userId, Constants.IS_FOLLOWING_USER);
                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        getView().onChangeFollowStatusError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                    }
                },error -> getView().onChangeFollowStatusError(Constants.RETROFIT_ERROR, error.getMessage())));
    }

    @Override
    public void unFollowUser(String userId) {
        SetUnfollowUserRequestModel request = new SetUnfollowUserRequestModel();
        request.setFollow_user_id(userId);
        addSubscription(AppsterWebServices.get().setUnfollowUser(mAuth, request)
                .subscribe(setFollowUserResponseModel -> {
                    if (setFollowUserResponseModel == null) {
                        return;
                    }
                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onChangeUnFollowStatusSuccessfully(userId, Constants.UN_FOLLOW_USER);
                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        getView().onChangeUnFollowStatusError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                    }
                },error -> getView().onChangeUnFollowStatusError(Constants.RETROFIT_ERROR, error.getMessage())));
    }
}
