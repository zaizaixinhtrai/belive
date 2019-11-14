package com.appster.features.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.features.blocked_screen.BlockedUserActivity;
import com.appster.features.edit_profile.ActivityEditProfile;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.LocaleUtil;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.PlayTokenAccountConnectModel;
import com.appster.webview.ActivityViewWeb;
import com.apster.common.Constants;
import com.apster.common.DialogManager;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;

//import com.gtoken.common.Playground;
//import com.gtoken.common.net.AccountRepository;
//import com.gtoken.common.net.response.ProfileResponse;
//import com.gtoken.common.view.login.Callback;


/**
 * Created by User on 10/9/2015.
 */
public class SettingActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SettingContract.SettingActivityView {

    private static final String TAG = SettingActivity.class.getSimpleName();

    @Bind(R.id.title_notify)
    TextView titleNotify;
    @Bind(R.id.txt_notifications)
    TextView txtNotifications;
    @Bind(R.id.txt_notification_sound)
    TextView txtNotificationSound;
    @Bind(R.id.txt_hide_message_details)
    TextView txtHideMessageDetails;
    @Bind(R.id.txt_Searchable)
    TextView txtSearchable;
    @Bind(R.id.txt_Messaging)
    TextView txtMessaging;
    @Bind(R.id.txt_language)
    TextView txtLanguage;
    @Bind(R.id.title_profile)
    TextView titleProfile;
    @Bind(R.id.title_suport)
    TextView titleSuport;
    @Bind(R.id.txt_title_lang)
    TextView txtTitleLang;
    @Bind(R.id.txt_edit_prifile)
    TextView txtEditPrifile;
    @Bind(R.id.rl_blocked_list)
    RelativeLayout llBlockedList;
    @Bind(R.id.txt_about)
    TextView txtAbout;
    @Bind(R.id.txt_Terms_And_Conditions)
    TextView txtTermsAndConditions;
    @Bind(R.id.txt_Deactivate)
    TextView txtDeactivate;
    @Bind(R.id.rl_version)
    RelativeLayout mRlVersionItem;
    @Bind(R.id.txt_version)
    TextView tvVersion;
    @Bind(R.id.txt_playtoken_connect)
    TextView txtPlaytokenConnect;

    private Switch sw_notifications;
    private Switch sw_notification_sound;
    private Switch sw_hide_message_details;
    private Switch sw_Searchable;
    private Switch sw_Messaging;
    @Bind(R.id.sw_live_notifications)
    Switch swLiveNotification;

    private TextView txt_language;

    private RelativeLayout fm_imv_arrow_language;
    private RelativeLayout fm_Edit_Profile;
    private RelativeLayout fm_About_Us;
    private RelativeLayout fm_Terms_And_Conditions;
    //    private RelativeLayout fm_Log_Out;
    private RelativeLayout fm_Deactivate_Account;

    private RelativeLayout layoutPlaytokenConnect;

    private UserModel userInforModel;

    private boolean isChangeLanguage = false;
    private boolean hasUnblockSomeone = false;

    private int languageCurrent;

    private SettingPresenter presenter;

    private CompositeSubscription mSubscription;

    //============== inherited methods =============================================================
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageCurrent = mAppPreferences.getUserModel().getLanguage();
        presenter = new SettingPresenter(this, AppsterWebServices.get());

        mSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mSubscription);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // chekc playtoken login status
//        AccountRepository playAccountRepo = Playground.getAccountRepository();
//        if (playAccountRepo != null) {
//            mCompositeSubscription.add(playAccountRepo.isLogin()
//                    .compose(AppsterApplication.get(this).applySchedulers())
//                    .subscribe(checkLoginResponse -> {
//                        Log.d(TAG, "checkLoginResponse.isIsLogin()=" + checkLoginResponse.isIsLogin());
//                        updatePlayTokenButtonText(checkLoginResponse.isIsLogin());
//                    }, error -> {
//                        updatePlayTokenButtonText(false);
//                    }));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.setting_slider));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());

        goneNotify(true);
        handleTurnoffMenuSliding();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_setting;
    }

    @Override
    public void init() {

        ButterKnife.bind(this);

        userInforModel = new UserModel();
        UserModel savedUserProfile = mAppPreferences.getUserModel();

        userInforModel.setVideoCall(savedUserProfile.getVideoCall());
        userInforModel.setVoiceCall(savedUserProfile.getVoiceCall());
        userInforModel.setMessaging(savedUserProfile.getMessaging());
        userInforModel.setSearchable(savedUserProfile.getSearchable());
        userInforModel.setHideMessageDetails(savedUserProfile.getHideMessageDetails());
        userInforModel.setLanguage(savedUserProfile.getLanguage());
        userInforModel.setNearbyFeature(savedUserProfile.getNearbyFeature());
        userInforModel.setNotificationSound(savedUserProfile.getNotificationSound());
        userInforModel.setNotification(savedUserProfile.getNotification());
        userInforModel.setLiveNotification(savedUserProfile.getLiveNotification());
        userInforModel.setUserId(savedUserProfile.getUserId());

        initID();

        setDefault(userInforModel);
        setClickSwitch();
    }

    @Override
    public View findViewInContentById(int id) {
        return super.findViewInContentById(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_BLOCKED_USER_LIST:
                if (data != null) {
                    hasUnblockSomeone = data.getBooleanExtra(BlockedUserActivity.ARG_UNBLOCK_USER, false);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);

        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        getLayoutContentId();
        initID();

        setTopBarTile(getString(R.string.settings_bold));
        titleNotify.setText(getString(R.string.setting_title_notifications));
        txtNotifications.setText(getString(R.string.setting_notifications));
        txtNotificationSound.setText(getString(R.string.setting_notification_sound));
        txtHideMessageDetails.setText(getString(R.string.setting_hide_message_details));
        txtSearchable.setText(getString(R.string.setting_Searchable));
        txtMessaging.setText(getString(R.string.setting_Messaging));
        titleProfile.setText(getString(R.string.setting_Profile));
        txtEditPrifile.setText(getString(R.string.setting_Edit_Profile));
        titleSuport.setText(getString(R.string.setting_Support));
        txtAbout.setText(getString(R.string.setting_About_Us));
        txtTermsAndConditions.setText(getString(R.string.setting_Terms_And_Conditions));
        txtDeactivate.setText(getString(R.string.setting_Deactivate_Account));
    }

    @Override
    public void onBackPressed() {

        Intent resultIntent = getIntent();
        resultIntent.putExtra(BlockedUserActivity.ARG_UNBLOCK_USER, hasUnblockSomeone);
//        if (isChangeLanguage) {
        savedChangeLanguage(isChangeLanguage);
        resultIntent.putExtra(ConstantBundleKey.BUNDLE_CHANGE_LANGUAGE, isChangeLanguage);
        setResult(RESULT_OK, resultIntent);
//        } else {
//            setResult(RESULT_CANCELED);
//        }
        finish();
    }

    @Override
    public void onDestroy() {

        if (isChangeLanguage) {
            savedChangeLanguage(isChangeLanguage);
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    //================ implemented methods =========================================================
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void showProgress(String message) {
        showDialog(this, message);
    }

    @Override
    public void hideProgress() {
        dismisDialog();
    }


    @Override
    public void onSettingUpdateSuccessfully() {
        if (languageCurrent != userInforModel.getLanguage()) {

            setLocale();
            Configuration newConfig = new Configuration();
            newConfig.locale = new Locale(mAppPreferences.getAppLanguage());
            onConfigurationChanged(newConfig);
            setChangeLanguage(userInforModel);
            languageCurrent = userInforModel.getLanguage();
            isChangeLanguage = true;
        }
        if (mAppPreferences.isUserLogin() && userInforModel.getUserId().equalsIgnoreCase(mAppPreferences.getUserModel().getUserId())) {
            //only update messaging setting
            UserModel userModel = mAppPreferences.getUserModel();
            userModel.setMessaging(userInforModel.getMessaging());
            mAppPreferences.saveUserInforModel(userModel);
        }
    }

    @Override
    public void onHasNewUpdates(boolean hasNewUpdates) {
        if (hasNewUpdates) {
            AppsterUtility.goToPlayStore(this, 500);
        } else {
            String title = getString(R.string.setting_check_update);
            String message = getString(R.string.setting_has_already_newest_version);
            String okButton = getString(R.string.btn_text_ok);
            DialogUtil.showConfirmDialogSingleAction(this, title, message, okButton, null);
        }
    }

    @Override
    public void onCheckUpdateFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //========== event handlers ====================================================================
    @Override
    public void onClick(View v) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent;
        switch (v.getId()) {
            case R.id.fm_imv_arrow_language:
                openChooseLanguage();
                break;

            case R.id.txt_language:
                openChooseLanguage();
                break;

            case R.id.layout_playtoken_connect:
                connectPlayToken();
                break;

            case R.id.fm_Edit_Profile:
                intent = new Intent(SettingActivity.this, ActivityEditProfile.class);
                startActivity(intent, options.toBundle());
                break;

            case R.id.rl_blocked_list:
                intent = new Intent(SettingActivity.this, BlockedUserActivity.class);
                startActivityForResult(intent, Constants.REQUEST_BLOCKED_USER_LIST, options.toBundle());
                break;

            case R.id.fm_About_Us:
                Intent intentViewWeb = new Intent(SettingActivity.this, ActivityViewWeb.class);
                intentViewWeb.putExtra(ConstantBundleKey.BUNDLE_URL_FOR_WEBVIEW, Constants.URL_ABOUT_US);
                startActivity(intentViewWeb, options.toBundle());
                break;

            case R.id.fm_Terms_And_Conditions:
                Intent intentTerms = new Intent(SettingActivity.this, ActivityViewWeb.class);
                intentTerms.putExtra(ConstantBundleKey.BUNDLE_URL_FOR_WEBVIEW, Constants.URL_TERMS_CONDITION);
                startActivity(intentTerms, options.toBundle());
                break;

            case R.id.fm_Deactivate_Account:
                presenter.confirmDeactivateAccount();
                break;

            case R.id.rl_version:
                AppsterUtility.temporaryLockView(v);
                checkUpdates();
                break;

            case R.id.slider_menu:
                onBackPressed();
                break;
        }
    }

    //============== inner methods =================================================================
    private void savedChangeLanguage(boolean isChangeLanguage) {
        mAppPreferences.setChangeLanguage(isChangeLanguage);
    }

    private void initID() {
        sw_notifications = (Switch) findViewById(R.id.sw_notifications);
        sw_notification_sound = (Switch) findViewById(R.id.sw_notification_sound);
        sw_hide_message_details = (Switch) findViewById(R.id.sw_hide_message_details);
        sw_Searchable = (Switch) findViewById(R.id.sw_Searchable);
        sw_Messaging = (Switch) findViewById(R.id.sw_Messaging);

        txt_language = (TextView) findViewById(R.id.txt_language);

        fm_imv_arrow_language = (RelativeLayout) findViewById(R.id.fm_imv_arrow_language);
        fm_Edit_Profile = (RelativeLayout) findViewById(R.id.fm_Edit_Profile);
        fm_About_Us = (RelativeLayout) findViewById(R.id.fm_About_Us);
        fm_Terms_And_Conditions = (RelativeLayout) findViewById(R.id.fm_Terms_And_Conditions);
        fm_Deactivate_Account = (RelativeLayout) findViewById(R.id.fm_Deactivate_Account);

        if (LocaleUtil.isChineseLanguage()) {
            layoutPlaytokenConnect = (RelativeLayout) findViewById(R.id.layout_playtoken_connect);
            layoutPlaytokenConnect.setOnClickListener(this);
            layoutPlaytokenConnect.setVisibility(View.VISIBLE);
        }

        fm_imv_arrow_language.setOnClickListener(this);
        txt_language.setOnClickListener(this);

        fm_Edit_Profile.setOnClickListener(this);
        llBlockedList.setOnClickListener(this);
        fm_About_Us.setOnClickListener(this);
        fm_Terms_And_Conditions.setOnClickListener(this);
        fm_Deactivate_Account.setOnClickListener(this);
        mRlVersionItem.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.sw_notifications:

                if (isChecked) {
                    userInforModel.setNotification(1);
                } else {
                    userInforModel.setNotification(0);
                }

                presenter.updateSetting(userInforModel);
                break;

            case R.id.sw_live_notifications:
                userInforModel.setLiveNotification(isChecked ? 1 : 0);
                presenter.updateSetting(userInforModel);
                break;

            case R.id.sw_notification_sound:

                if (isChecked) {
                    userInforModel.setNotificationSound(1);
                } else {
                    userInforModel.setNotificationSound(0);
                }

                presenter.updateSetting(userInforModel);
                break;

            case R.id.sw_hide_message_details:

                if (isChecked) {
                    userInforModel.setHideMessageDetails(1);
                } else {
                    userInforModel.setHideMessageDetails(0);
                }

                presenter.updateSetting(userInforModel);
                break;

            case R.id.sw_Searchable:

                if (isChecked) {
                    userInforModel.setSearchable(1);
                } else {
                    userInforModel.setSearchable(0);
                }

                presenter.updateSetting(userInforModel);

                break;

            case R.id.sw_Messaging:

                if (isChecked) {
                    userInforModel.setMessaging(1);
                } else {
                    userInforModel.setMessaging(0);
                }

                presenter.updateSetting(userInforModel);

                break;
        }
    }

    private void checkUpdates() {
        presenter.checkUpdates();
    }

    private void setDefault(UserModel userInforModel) {

        if (userInforModel.getNotification() == 1) {
            sw_notifications.setChecked(true);
        }

        if (userInforModel.getNotificationSound() == 1) {
            sw_notification_sound.setChecked(true);
        }

        swLiveNotification.setChecked(userInforModel.getLiveNotification() == 1);

        if (userInforModel.getHideMessageDetails() == 1) {
            sw_hide_message_details.setChecked(true);
        }

        if (userInforModel.getSearchable() == 1) {
            sw_Searchable.setChecked(true);
        }

        if (userInforModel.getMessaging() == 1) {
            sw_Messaging.setChecked(true);
        }

        setChangeLanguage(userInforModel);

//        if (BuildConfig.APPLICATION_ID.equals("com.appster.staging") || BuildConfig.APPLICATION_ID.equals("com.appster.dev")) {
//            version.setVisibility(View.VISIBLE);
//            try {
//                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                version.setText(packageInfo.versionName + " " + getString(R.string.app_name));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            version.setVisibility(View.GONE);
//        }

        try {
            tvVersion.setText(getString(R.string.setting_version, AppsterApplication.getCurrentVersionName(this)));
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
    }

    private void setClickSwitch() {
        sw_notifications.setOnCheckedChangeListener(this);
        sw_notification_sound.setOnCheckedChangeListener(this);
        swLiveNotification.setOnCheckedChangeListener(this);
        sw_hide_message_details.setOnCheckedChangeListener(this);
        sw_Searchable.setOnCheckedChangeListener(this);
        sw_Messaging.setOnCheckedChangeListener(this);
    }

    public void openChooseLanguage() {

        final Dialog dialog = new Dialog(SettingActivity.this, R.style.Theme_DialogCustom);
        dialog.setContentView(R.layout.choose_language);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Button btn_english = (Button) dialog.findViewById(R.id.btn_english);
        Button btn_sp = (Button) dialog.findViewById(R.id.btn_sp);
        Button btn_zh = (Button) dialog.findViewById(R.id.btn_zh);
        RelativeLayout relativelayout_photo_dialog_main = (RelativeLayout) dialog.findViewById(R.id.relativelayout_photo_dialog_main);
        Button button_cancel_photo = (Button) dialog.findViewById(R.id.button_cancel_photo);

        btn_english.setOnClickListener(v -> {
            userInforModel.setLanguage(Constants.APP_LANGUAGE_TYPE_ENGLISH);

            presenter.updateSetting(userInforModel);
            dialog.dismiss();
        });

        btn_sp.setOnClickListener(v -> {
            userInforModel.setLanguage(Constants.APP_LANGUAGE_TYPE_SIMPLIFIED);
            presenter.updateSetting(userInforModel);
            dialog.dismiss();
        });

        btn_zh.setOnClickListener(v -> {
            userInforModel.setLanguage(Constants.APP_LANGUAGE_TYPE_TRADITIONAL);
            presenter.updateSetting(userInforModel);
            dialog.dismiss();
        });

        relativelayout_photo_dialog_main.setOnClickListener(v -> dialog.dismiss());

        button_cancel_photo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setChangeLanguage(UserModel userInforModel) {

        txtTitleLang.setText(getString(R.string.setting_Language));

        switch (userInforModel.getLanguage()) {

            case Constants.APP_LANGUAGE_TYPE_ENGLISH:

                txt_language.setText(R.string.activity_choose_language_english);

                break;

            case Constants.APP_LANGUAGE_TYPE_TRADITIONAL:

                txt_language.setText(R.string.activity_choose_language_zh);

                break;

            case Constants.APP_LANGUAGE_TYPE_SIMPLIFIED:

                txt_language.setText(R.string.activity_choose_language_sp);

                break;
        }
    }

    private void connectPlayToken() {

//        Playground.showLoginDialog(this, LocaleUtil.getLocaleString(), new Callback<ProfileResponse>() {
//            @Override
//            public void onLoginSuccess(ProfileResponse profileResponse) {
//                // success
//                updatePlayTokenId(profileResponse.getProfile().getAccount());
//            }
//
//            @Override
//            public void onRegisterSuccess(ProfileResponse profileResponse) {
//                // success
//                updatePlayTokenId(profileResponse.getProfile().getAccount());
//            }
//
//            @Override
//            public void onError(String s) {
//                // error occurred during playtoken login
//                txtPlaytokenConnect.setText(R.string.setting_playtoken_connect);
//            }
//        });
    }

    private void updatePlayTokenId(String playTokenId) {

        PlayTokenAccountConnectModel request = new PlayTokenAccountConnectModel(playTokenId);

        mSubscription.add(AppsterWebServices.get().playTokenAccountConnect(mAppPreferences.getUserTokenRequest(), request)
                        .subscribe(response -> {

                            Log.d(TAG, "playTokenAccountConnect code=" + response.getCode() + ", message=" + response.getMessage());

                            if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                updatePlayTokenButtonText(true);

                                // save in preferences
                                mAppPreferences.setPlayTokenUserName(playTokenId);

                            } else {
                                handleError(response.getMessage(), response.getCode());
                                updatePlayTokenButtonText(false);

                                // delete from preferences
                                mAppPreferences.setPlayTokenUserName(null);

                                //logout
//                        AccountRepository playAccountRepo = Playground.getAccountRepository();
//                        if (playAccountRepo != null) {
//                            playAccountRepo.logOut()
//                                    .compose(AppsterApplication.get(this).applySchedulers())
//                                    .subscribe(baseResponse -> {
//                                    }, throwable -> {
//                                    });
//                        }
                            }

                        }, error -> {
                            DialogManager.getInstance().dismisDialog();
                            handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                        })
        );
    }

    private void updatePlayTokenButtonText(boolean isConnected) {
        if (isConnected) {
            txtPlaytokenConnect.setText(R.string.setting_playtoken_connected);
        } else {
            txtPlaytokenConnect.setText(R.string.setting_playtoken_connect);
        }
    }
}
