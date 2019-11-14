package com.appster.features.edit_profile;

import com.appster.AppsterApplication;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.EditProfileRequestModel;
import com.apster.common.Constants;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by linh on 06/12/2016.
 */

public class EditProfilePresenter implements EditProfileContract.UserActions {
    private EditProfileContract.EditProfileView view;
    private AppsterWebserviceAPI mService;
    private CompositeSubscription compositeSubscription;
    private String mAuthen;

    public EditProfilePresenter(EditProfileContract.EditProfileView view, AppsterWebserviceAPI appsterWebserviceAPI) {
        this.view = view;
        this.mService = appsterWebserviceAPI;
        this.compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mAuthen = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
    }

    @Override
    public void updateProfile(EditProfileRequestModel request) {
        view.showProgress();
        compositeSubscription.add(AppsterWebServices.get().editProfile(mAuthen, request.build())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(editProfileDataResponse -> {
                    view.hideProgress();
                    if (editProfileDataResponse == null) return;
                    if (editProfileDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        view.onUpdateCompleted(editProfileDataResponse.getData().getUserInfo());
//
                    } else {
                        view.loadError(editProfileDataResponse.getMessage(), editProfileDataResponse.getCode());
                    }
                }, throwable -> {
                    view.hideProgress();
                    Timber.d(throwable.getMessage());
                    view.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    public void attachView(EditProfileContract.EditProfileView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
