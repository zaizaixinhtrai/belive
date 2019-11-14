package com.appster.features.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.features.login.LoginActivity;
import com.appster.models.AppConfigModel;
import com.appster.models.UserModel;
import com.appster.utility.OneSignalUtil;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogbeLiveConfirmation;
import com.data.repository.AppConfigsDataRepository;
import com.data.repository.SettingDataRepository;
import com.data.repository.datasource.cloud.CloudAppConfigsDataSource;
import com.data.repository.datasource.cloud.CloudSettingDataSource;
import com.domain.interactors.AppConfigsUseCase;
import com.domain.interactors.setting.DeactivateAccountUseCase;
import com.domain.interactors.setting.SettingUseCase;
import com.domain.repository.SettingRepository;
import com.pack.utility.CheckNetwork;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by linh on 12/12/2016.
 */

public class SettingPresenter implements SettingContract.UserActions {
    private Context context;
    private SettingContract.SettingActivityView view;
    private AppsterWebserviceAPI mService;
    private CompositeSubscription compositeSubscription;
    private String authen;
    private final SettingUseCase mSettingUseCase;
    private final DeactivateAccountUseCase mDeactivateAccountUseCase;
    private final AppConfigsUseCase mAppConfigsUseCase;

    public SettingPresenter(SettingContract.SettingActivityView view, AppsterWebserviceAPI mService) {
        attachView(view);
        context = (Context) view;
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        this.mService = mService;
        this.compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        this.authen = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
        SettingRepository settingRepository = new SettingDataRepository(new CloudSettingDataSource(mService, authen));
        mSettingUseCase = new SettingUseCase(uiThread, ioThread, settingRepository);
        AppConfigsDataRepository appConfigsDataRepository = new AppConfigsDataRepository(new CloudAppConfigsDataSource(mService, AppsterUtility.getAuth()));
        mDeactivateAccountUseCase = new DeactivateAccountUseCase(uiThread, ioThread, settingRepository);
        mAppConfigsUseCase = new AppConfigsUseCase(uiThread, ioThread, appConfigsDataRepository);
    }

    @Override
    public void attachView(SettingContract.SettingActivityView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        context = null;
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void checkUpdates() {
        compositeSubscription.add(mAppConfigsUseCase.execute(null)
                .subscribe(appConfigModelBaseResponse -> {
                    try {
                        AppConfigModel appConfig = appConfigModelBaseResponse.getData();
                        String newestVersion = appConfig.newestBeliveAndroidVersion;
                        String currentVersion = AppsterApplication.getCurrentVersionName(context);
                        int compareResult = compareVersionNames(currentVersion, newestVersion);
                        view.onHasNewUpdates(compareResult == -1);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                })

        );
    }

    void updateSetting(UserModel userInforModel) {

        view.showProgress(context.getResources().getString(R.string.connecting_msg));

        SettingFeaturesRequestModel request = new SettingFeaturesRequestModel();
        request.mVideoCall = userInforModel.getVideoCall();
        request.mVoiceCall = userInforModel.getVoiceCall();
        request.mMessaging = userInforModel.getMessaging();
        request.mSearchable = userInforModel.getSearchable();
        request.mHideMessageDetails = userInforModel.getHideMessageDetails();
        request.mLanguage = userInforModel.getLanguage();
        request.mNearbyFeature = userInforModel.getNearbyFeature();
        request.mNotificationSound = userInforModel.getNotificationSound();
        request.mNotification  = userInforModel.getNotification();
        request.liveNotification = userInforModel.getLiveNotification();
        request.mUserId = userInforModel.getUserId();

        compositeSubscription.add(mSettingUseCase.execute(request)
                .subscribe(settingFeatureDataResponse -> {
                    view.hideProgress();
                    if (settingFeatureDataResponse == null) return;
                    if (settingFeatureDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        saveUserInfo(userInforModel);
                        OneSignalUtil.setUser(userInforModel);
                        view.onSettingUpdateSuccessfully();

                    } else {
                        view.loadError(settingFeatureDataResponse.getMessage(), settingFeatureDataResponse.getCode());
                    }
                }, throwable -> {
                    view.hideProgress();
                    view.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    void confirmDeactivateAccount() {

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        DialogbeLiveConfirmation confirmation = new DialogbeLiveConfirmation(builder);
        builder.title(context.getString(R.string.app_name))
                .message(context.getString(R.string.setting_confirm_Deactivate_Account))
                .confirmText(context.getString(R.string.btn_text_ok))
                .singleAction(false)
                .onConfirmClicked(() -> {
                    if (CheckNetwork.isNetworkAvailable(context)) {
                        deactivateAccount();

                    } else {

                        ((BaseToolBarActivity)context).utility.showMessage(
                                context.getString(R.string.app_name),
                                context.getResources().getString(
                                        R.string.no_internet_connection),
                                context);
                    }
                })
                .build().show(context);
    }

    private void deactivateAccount() {
        view.showProgress(context.getResources().getString(R.string.connecting_msg));
        DeactivateAccountRequsetModel request = new DeactivateAccountRequsetModel();
        compositeSubscription.add(mDeactivateAccountUseCase.buildObservable(request)
                .subscribe(deactivateAccountDataResponse -> {
                    view.hideProgress();
                    if (deactivateAccountDataResponse.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        view.loadError(deactivateAccountDataResponse.getMessage(), deactivateAccountDataResponse.getCode());
                    } else {
                        AppsterWebServices.resetAppsterWebserviceAPI();
                        AppsterApplication.mAppPreferences.clearAllParamLogin();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                }, throwable -> {
                    view.hideProgress();
                    view.loadError(throwable.getMessage(), Constants.RETROFIT_ERROR);
                }));

    }

    private void saveUserInfo(UserModel userInfo) {
        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
        userModel.setVideoCall(userInfo.getVideoCall());
        userModel.setVoiceCall(userInfo.getVoiceCall());
        userModel.setMessaging(userInfo.getMessaging());
        userModel.setSearchable(userInfo.getSearchable());
        userModel.setHideMessageDetails(userInfo.getHideMessageDetails());
        userModel.setLanguage(userInfo.getLanguage());
        userModel.setNearbyFeature(userInfo.getNearbyFeature());
        userModel.setNotificationSound(userInfo.getNotificationSound());
        userModel.setNotification(userInfo.getNotification());
        userModel.setUserId(userInfo.getUserId());
        userModel.setLiveNotification(userInfo.getLiveNotification());

        AppsterApplication.mAppPreferences.saveUserInforModel(userModel);
    }

    /**
     * @return 1 if the #oldVersionName is greater than @newVersionName
     * 0 if the @{link oldVersionName} and newVersionName are equal
     * otherwise return -1
     */
    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.parseInt(oldNumbers[i]);
            int newVersionPart = Integer.parseInt(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length)?1:-1;
        }

        return res;
    }


    //    private void confirmLogout() {
//
//        DialogYesNo confirmDel = new DialogYesNo(SettingActivity.this);
//        confirmDel.showDialog(getString(
//                R.string.are_you_sure_you_want_to_logout));
//
//        confirmDel.setOnclickConfirm(new DialogYesNo.OnclickConfirm() {
//            @Override
//            public void onClickOk() {
//
//                if (CheckNetwork.isNetworkAvailable(SettingActivity.this)) {
//                    logout();
//                    AppsterApplication.logout();
//                    finish();
//
//                } else {
//
//                    utility.showMessage(
//                            getString(R.string.app_name),
//                            getResources().getString(
//                                    R.string.no_internet_connection),
//                            SettingActivity.this);
//                }
//
//            }
//
//            @Override
//            public void onClickCancel() {
//
//            }
//        });
//
//    }

    //    private void logout() {
//        //  DialogManager.getInstance().showDialog(SettingActivity.this, getResources().getString(R.string.connecting_msg));
//        showDialog(this, getResources().getString(R.string.connecting_msg));
//        LogoutRequestModel request = new LogoutRequestModel();
//        AppsterWebServices.get().logoutApp("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request,
//                new Callback<BaseResponse<Boolean>>() {
//                    @Override
//                    public void success(LogoutDataResponse logoutResponseModel, Response response) {
//                        // DialogManager.getInstance().dismisDialog();
//                        dismisDialog();
//
//                        if (logoutResponseModel == null) return;
//
//                        if (logoutResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
//                            handleError(logoutResponseModel.getMessage(), logoutResponseModel.getCode());
//                        }
//                        SocialManager.getInstance().logOut();
//                        SocialManager.logoutGoogle(SettingActivity.this, null);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        DialogManager.getInstance().dismisDialog();
//
//                        handleError(error.getMessage(), Constants.RETROFIT_ERROR);
//                    }
//                });
//    }
}
