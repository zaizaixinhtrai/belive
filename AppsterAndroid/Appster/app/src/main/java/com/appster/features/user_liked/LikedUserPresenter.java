package com.appster.features.user_liked;

import com.appster.AppsterApplication;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.WallFeedManager;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.LikedStreamUsersRequestModel;
import com.appster.webservice.request_models.LikedUsersRequestModel;
import com.appster.domain.LikedUsersItemModel;
import com.appster.models.FollowUser;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.apster.common.Constants;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ThanhBan on 9/27/2016.
 */

public class LikedUserPresenter implements StreamLikedScreenContract.UserActions {


    AppsterWebserviceAPI mService;
    StreamLikedScreenContract.StreamLikedScreenView mView;
    Subscription subscription;
    private String mAuthen;
    int nextPage = 0;
    boolean isEndList = false;
    boolean isStream =false;
    String slug;

    public LikedUserPresenter(StreamLikedScreenContract.StreamLikedScreenView view, AppsterWebserviceAPI service,boolean isStream,String slug) {
        attachView(view);
        this.mService = service;
        mAuthen =  AppsterApplication.mAppPreferences.getUserTokenRequest();
        this.isStream = isStream;
        this.slug = slug;
    }

    @Override
    public void attachView(StreamLikedScreenContract.StreamLikedScreenView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    @Override
    public void getLikedUsers(int delayTime, final int postId) {
        if (isEndList)
            return;

        if (delayTime != 0) {
            mView.showLoadingItem();
            RxUtils.unsubscribeIfNotNull(subscription);
            subscription = Observable.just(postId).delay(delayTime, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::getLikedUsers, error -> mView.loadError(error.getMessage(), 9999));
        } else {
            getLikedUsers(postId);
        }
    }

    @Override
    public void refreshData(int postId){
        nextPage = 0;
        isEndList = false;
        getLikedUsers(postId);
    }

    @Override
    public void followButtonClicked(LikedUsersItemModel itemModel, final int position) {
        FollowUser followUser = new FollowUser(mView.getViewContext(), itemModel.getUserId(), itemModel.getIs_follow() != 1);
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                if (mView != null) {
                    mView.followChanged(isFollow, position);
                    FollowStatusChangedEvent eventFollow = new FollowStatusChangedEvent();
                    eventFollow.setUserId(itemModel.getUserId());
                    eventFollow.setStream(false);
                    eventFollow.setFollowType(isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
                    WallFeedManager.getInstance().updateFollowStatus(eventFollow);
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                if (mView != null) {
                    mView.loadError(message, errorCode);
                }
            }
        });
        followUser.execute();

    }
    private void handleDataResult(BaseResponse<BaseDataPagingResponseModel<LikedUsersItemModel>> likedUsersDataRespone){
        if (mView != null) {
            mView.dismissLoadingItem();
            if (likedUsersDataRespone == null) {
                mView.loadError("No data response!!", 9999);
            } else {
                if (likedUsersDataRespone.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                    mView.likedUsers(likedUsersDataRespone.getData().getResult());
                    nextPage = likedUsersDataRespone.getData().getNextId();
                    isEndList = likedUsersDataRespone.getData().isEnd();
                }
            }
        }
    }


    void getLikedUsers(int postId) {
        if (isStream) {
            LikedStreamUsersRequestModel requestModel = new LikedStreamUsersRequestModel(slug);
            if (nextPage != 0) {
                requestModel.setNextId(nextPage);
            }
            mService.getLikedStreamUsers(mAuthen, requestModel)
                    .subscribe(this::handleDataResult, error -> {
                        if (mView != null) {
                            mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                        }
                    });

        } else {

            LikedUsersRequestModel requestModel = new LikedUsersRequestModel(postId);
            if (nextPage != 0) {
                requestModel.setNextId(nextPage);
            }


            mService.getLikedUsers(mAuthen, requestModel)
                    .subscribe(this::handleDataResult, error -> {
                        if (mView != null) {
                            mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                        }
                    });
        }
    }

}
