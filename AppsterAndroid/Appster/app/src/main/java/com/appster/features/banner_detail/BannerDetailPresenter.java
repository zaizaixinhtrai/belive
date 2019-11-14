package com.appster.features.banner_detail;

import com.appster.AppsterApplication;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.SetFollowUserRequestModel;

public class BannerDetailPresenter extends BasePresenter<BannerDetailContract.View> implements BannerDetailContract.UserActions {

    @Override
    public void followUser(String userId) {
        getView().showProgress();
        SetFollowUserRequestModel requestModel = new SetFollowUserRequestModel();
        requestModel.setFollow_user_id(userId);
        addSubscription(AppsterWebServices.get().setFollowUser(AppsterApplication.mAppPreferences.getUserTokenRequest(), requestModel)
                .subscribe(setFollowUserResponseModel -> {
                    getView().hideProgress();
                    getView().followUserResult();
                }, error -> {
                    getView().followUserResult();
                    getView().hideProgress();
                }));
    }
}
