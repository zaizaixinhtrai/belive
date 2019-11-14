package com.appster.models;

import android.content.Context;

import com.appster.AppsterApplication;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.SetFollowUserRequestModel;
import com.appster.webservice.request_models.SetUnfollowUserRequestModel;
import com.apster.common.Constants;

/**
 * Created by User on 9/8/2015.
 */
public class FollowUser {
    private Context mContext;
    private String follow_user_id;
    private boolean isFollow = true;
    private OnSetFollowUserListener setFollowUserListener;

    public void setSetFollowUserListener(OnSetFollowUserListener setFollowUserListener) {
        this.setFollowUserListener = setFollowUserListener;
    }

    public FollowUser(Context context, String follow_user_id, boolean isFollow) {
        this.mContext = context;
        this.follow_user_id = follow_user_id;
        this.isFollow = isFollow;
    }

    public void execute() {

        if (isFollow) {
            follow();
        } else {
            unFollow();
        }

    }
    public void executeFollowWithPass(String pass){
        if(isFollow){
            followWithPassword(pass);
        }
    }

    private void follow() {

        SetFollowUserRequestModel request = new SetFollowUserRequestModel();
        request.setFollow_user_id(follow_user_id);
        AppsterWebServices.get().setFollowUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(setFollowUserResponseModel -> {

                    if (setFollowUserResponseModel == null) {
                        return;
                    }

                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (setFollowUserListener != null) {
                            setFollowUserListener.onFinishFollow(isFollow);
                        }

                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        if (setFollowUserListener != null) {
                            setFollowUserListener.onError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                        }
                    }
                },error -> {
                    if (setFollowUserListener != null) {
                        setFollowUserListener.onError(Constants.RETROFIT_ERROR, error.getMessage());
                    }
                });
    }
    private void followWithPassword(String pass){
        SetFollowUserRequestModel request = new SetFollowUserRequestModel();
        request.setFollow_user_id(follow_user_id);
        request.setPrivatePassword(pass);
        AppsterWebServices.get().setFollowUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(setFollowUserResponseModel -> {

                    if (setFollowUserResponseModel == null) {
                        return;
                    }

                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (setFollowUserListener != null) {
                            setFollowUserListener.onFinishFollow(isFollow);
                        }

                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        if (setFollowUserListener != null) {
                            setFollowUserListener.onError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                        }
                    }
                },error -> {
                    if (setFollowUserListener != null) {
                        setFollowUserListener.onError(Constants.RETROFIT_ERROR, error.getMessage());
                    }
                });
    }

    private void unFollow() {

        SetUnfollowUserRequestModel request = new SetUnfollowUserRequestModel();
        request.setFollow_user_id(follow_user_id);

        AppsterWebServices.get().setUnfollowUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(setUnfollowUserResponseModel -> {

                    if (setUnfollowUserResponseModel == null) return;

                    if (setUnfollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (setFollowUserListener != null) {
                            setFollowUserListener.onFinishFollow(isFollow);
                        }

                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setUnfollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        if (setFollowUserListener != null) {
                            setFollowUserListener.onError(setUnfollowUserResponseModel.getCode(), setUnfollowUserResponseModel.getMessage());
                        }
                    }
                },error -> {
                    if (setFollowUserListener != null) {
                        setFollowUserListener.onError(Constants.RETROFIT_ERROR, error.getMessage());
                    }
                });

    }

}
