package com.appster.flashscreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.data.AppPreferences;
import com.appster.features.login.LoginActivity;
import com.appster.features.maintenance.MaintenanceActivity;
import com.appster.main.MainActivity;
import com.appster.manager.AppLanguage;
import com.appster.manager.AppsterChatManger;
import com.appster.manager.ShowErrorManager;
import com.appster.tracking.EventTracker;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DeviceInfo;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.VersionRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.VersionResponseModel;
import com.apster.common.Constants;
import com.apster.common.LogUtils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.SetLocal;
import com.stephentuso.welcome.WelcomeHelper;

import org.jivesoftware.smack.util.StringUtils;

import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int TIME_SHOW_ANIMATION_START_APP = 2900;
    private static final int TIME_SHOW_ANIMATION_NORMAL = 1000;
    private String goingScreen = "";
    private String postDetailID = "";
    private String Slug = "";
    private String StreamURL = "";
    private String userID = "";
    private boolean isRecorded;
    boolean shouldGoToNextPage = false;

    String mDeviceName = "";
    String mDeviceUUID = "";

    private static final int REQUEST_ONBOARDING_SCREEN_RESULT = 500;
    private static final int REQUEST_GOOGLE_STORE = 999;
    private WelcomeHelper mWelcomeHelper;

    private CompositeSubscription mCompositeSubscription;

    public DialogInfoUtility utility;

    private int mTimeForShowAnimation;
    protected AppsterWebserviceAPI mService;

    public static Intent createIntent(Context context) {
        return new Intent(context, SplashScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Timber.e("Activity is not the root.  Finishing Activity instead of launching.");
                finish();
                return;
            }
        }

        // init Amplitude tracking
        mService = AppsterWebServices.get();
        setContentView(R.layout.activity_appster);
        checkFirstRunForShowAnimation();
        splashScreenCheckData();
        getNewWallfeed();
    }

    private void splashScreenCheckData() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        utility = new DialogInfoUtility();
        mDeviceUUID = DeviceInfo.getDeviceDetail(this);
        mAppPreferences.setDevicesUDID(mDeviceUUID);
        mDeviceName = AppsterApplication.getDeviceName();

        checkMaintenance();
//        getAppConfigFromServer();
        if (mAppPreferences.isUserLogin()) {
            checkEndGift();
        } else {
            shouldGoToNextPage = true;
        }
        getResourcesLoadUserImage();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mWelcomeHelper.onSaveInstanceState(outState);
    }

    private void checkFirstRunForShowAnimation() {

        int currentVersionCode = BuildConfig.VERSION_CODE;
        int savedVersionCode = mAppPreferences.getCurrentVersionCode();
        ImageView ivSplashImage = (ImageView) findViewById(R.id.ivSplashImage);

//        if (savedVersionCode == -1) {
//
//            // This is a new install (or the user cleared the shared preferences)
//            mTimeForShowAnimation = TIME_SHOW_ANIMATION_START_APP;
//            ivSplashImage.setBackground(ContextCompat.getDrawable(this, R.drawable.splash_animation_first_open));
//            // Start animation
//            ivSplashImage.post(() -> ((AnimationDrawable) ivSplashImage.getBackground()).start());
//
//        } else if (currentVersionCode >= savedVersionCode) {
        // This is just a normal run
        mTimeForShowAnimation = TIME_SHOW_ANIMATION_NORMAL;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        ivSplashImage.setLayoutParams(params);
        ivSplashImage.setImageDrawable(null);
        ivSplashImage.setBackground(ContextCompat.getDrawable(this, R.drawable.topbar_belive_logo));


//        }
//        else if (currentVersionCode > savedVersionCode) {
//
//            // This is an upgrade
//            mTimeForShowAnimation = TIME_SHOW_ANIMATION_NORMAL;
//            ivSplashImage.setBackground(ContextCompat.getDrawable(this, R.drawable.frame_47));
//        }

        // Update the shared preferences with the current version code
        mAppPreferences.saveCurrentVersionCode(currentVersionCode);
    }

    private void getNewWallfeed() {
        mCompositeSubscription.add(AppsterWebServices.get().getNewWallfeed("Bearer " + AppsterApplication.mAppPreferences.getUserToken())
                .observeOn(Schedulers.newThread())
                .subscribe(unreadMessage -> {
                    if (unreadMessage.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        AppsterApplication.mAppPreferences.setIsIsNewPostFromFollowingUsers(unreadMessage.getData().numberNewItem > 0);
                        Timber.e("unreadMessage.getData()=" + unreadMessage.getData().numberNewItem);
                    }
                }, error -> {
                    Timber.e(error);
                }));
    }

    private void getResourcesLoadUserImage() {
        mCompositeSubscription.add(AppsterWebServices.get().getResourcesLoadUserImage()
                .filter(resourcesLoadUserImage -> resourcesLoadUserImage != null)
                .subscribe(resourcesLoadUserImage -> {
                    if (resourcesLoadUserImage != null) {
                        Constants.AWS_S3_SERVER_LINK = resourcesLoadUserImage.getProfileImageUrl();
                        Constants.HOURS_CACHE_STATIS_RESOURCES = resourcesLoadUserImage.getHoursCacheStatisResources();
                    }
                }, error -> {
                    Timber.e(error);
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.e("onResume!!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ONBOARDING_SCREEN_RESULT) {

            if (resultCode == RESULT_OK) {
                if (shouldGoToNextPage) {
                    navigateApplication();
                }
            }

        } else if (requestCode == REQUEST_GOOGLE_STORE) {
            checkVersion();
        }
    }

    private void checkEndGift() {
        mCompositeSubscription.add(Observable.just(1).delay(mTimeForShowAnimation, TimeUnit.MILLISECONDS).subscribe(integer -> {
            if (shouldGoToNextPage) {
                navigateApplication();
            } else {
                shouldGoToNextPage = true;
            }
        }, error -> handleError(error.getMessage(), ShowErrorManager.un_know_error)));
    }


    private void checkMaintenance() {

        if (!CheckNetwork.isNetworkAvailable(getApplicationContext())) {
            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this);
            return;
        }

        long time = SystemClock.currentThreadTimeMillis();
        mCompositeSubscription.add(AppsterWebServices.get().checkMaintenance(BuildConfig.AWS_S3_SERVER_LINK + "maintenance/maintenance.json?t=" + time)
//                .onErrorResumeNext(Observable::error)
                .subscribe(maintenance -> {
                    if (maintenance == null) {
                        checkVersion();
                        return;
                    }

                    mAppPreferences.saveMaintenanceModel(maintenance);
                    if (maintenance.maintenanceMode == Constants.MAINTENANCE_MODE_START) {
                        MaintenanceActivity.startMaintenanceActivity(this, maintenance);
                        return;
                    }

                    checkVersion();

                }, error -> {
                    Timber.d(error);
                    checkVersion();
                }));

    }

    private void getAppConfigFromServer() {
        mCompositeSubscription.add(AppsterWebServices.get().getAppConfigs(AppsterUtility.getAuth())
                .filter(appConfigModelBaseResponse -> appConfigModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && appConfigModelBaseResponse.getData() != null)
                .map(BaseResponse::getData)
                .subscribe(appConfig -> mAppPreferences.storeRemoteConfig(appConfig), Timber::e));
    }

    private void checkVersion() {
//        tvStatusText.setText("Checking for new updates ");
        if (!CheckNetwork.isNetworkAvailable(getApplicationContext())) {
            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this);
            return;
        }
        String version = "";
        try {
            version = AppsterApplication.getCurrentVersionName(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        VersionRequestModel requestModel = new VersionRequestModel("0", version, mDeviceUUID, mDeviceName, String.valueOf(Build.VERSION.RELEASE), BuildConfig.APP_TYPE);
        String auth = "Bearer " + mAppPreferences.getUserToken();
        if (mService != null && !isDestroyed() && !isFinishing()) {
            mCompositeSubscription.add(mService.checkVersion(auth, requestModel)
                    .subscribe(versionDataResponse -> {
                        if (versionDataResponse != null && versionDataResponse.getData() != null) {

                            VersionResponseModel resData = versionDataResponse.getData();
                            AppPreferences pref = AppPreferences.getInstance(this);

                            // update country code
                            pref.setUserCountryCode(resData.getCountryCode());

                            if (versionDataResponse.getData().getForceUpdate()) {
                                showForceUpdateDialog(versionDataResponse.getData().getMessage());
                            } else {
//                            if (!mAppPreferences.isUserLogin()) {
//                                mWelcomeHelper = new WelcomeHelper(this, OnBoardingActivity.class);
////                                mWelcomeHelper.forceShow(REQUEST_ONBOARDING_SCREEN_RESULT);
//                                mWelcomeHelper.forceShowAndCloseActivity();
////                                finish();
////                                overridePendingTransition(R.anim.wel_none, R.anim.push_in_to_left);
//                                return;
//                            }
                                if (shouldGoToNextPage) {
                                    navigateApplication();
                                } else {
                                    shouldGoToNextPage = true;
                                }
                            }
                        }
                        if(versionDataResponse.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK){
                            handleError(versionDataResponse.getMessage(),versionDataResponse.getCode());
                        }
                    }, error -> handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
        }

    }

    private void checkReferralId() {
        Branch.getInstance().initSession((branchUniversalObject, linkProperties, branchError) -> {
            //If not Launched by clicking Branch link
            if (branchUniversalObject == null) {
                mAppPreferences.setReferralId("");
                gotoLogInScreen();
            }
            /* In case the clicked link has $android_deeplink_path the Branch will launch the MonsterViewer automatically since AutoDeeplinking feature is enabled.
             * Launch Monster viewer activity if a link clicked without $android_deeplink_path
             */
            else if (branchUniversalObject.getContentMetadata().getCustomMetadata().containsKey(Constants.REFERRAL_ID)) {
                Timber.d("referral id: %s", branchUniversalObject.getContentMetadata().getCustomMetadata().get(Constants.REFERRAL_ID));
                mAppPreferences.setReferralId(branchUniversalObject.getContentMetadata().getCustomMetadata().get(Constants.REFERRAL_ID));
                gotoLogInScreen();
            }
        }, this.getIntent().getData(), this);
    }

    public void handleError(String errorMessage, int errorCode) {
        if (this.isFinishing()) {
            return;
        }
        Timber.e(errorMessage);
        utility = new DialogInfoUtility();
        if (errorCode == Constants.RETROFIT_ERROR) {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                utility.showMessage(getString(R.string.app_name), getString(R.string.check_your_connection), this);
            } else {
                utility.showMessage(getString(R.string.app_name), getString(R.string.activity_sign_unknown_error), this);
            }
        } else if (errorCode == ShowErrorManager.account_deactivate_or_invalid_email ||
                errorCode == ShowErrorManager.account_deactivated_or_suspended ||
                errorCode == ShowErrorManager.authentication_error) {
            View.OnClickListener mclick = v -> AppsterApplication.logout(v.getContext());

            utility.showMessage(getString(R.string.app_name), getString(R.string.account_authentication_problem), this, mclick);

        } else {
            utility.showMessage(getString(R.string.app_name),errorMessage,this);
        }

    }

    void showForceUpdateDialog(String message) {
        View.OnClickListener clickHandle = v -> {
            RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
            AppsterUtility.goToPlayStore(SplashScreenActivity.this, REQUEST_GOOGLE_STORE);
        };
        utility.showForceUpdateMessage(getString(R.string.app_name), message, this, clickHandle);
        mCompositeSubscription.add(Observable.just(1).delay(2900, TimeUnit.MILLISECONDS).subscribe(integer -> {
            RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
            AppsterUtility.goToPlayStore(SplashScreenActivity.this, REQUEST_GOOGLE_STORE);
        }, error -> handleError(error.getMessage(), ShowErrorManager.un_know_error)));
    }

    void navigateApplication() {
        boolean isHandleIntentNavigate = handleIntentNavigate();
        if (mAppPreferences.isUserLogin() || "stream".equals(goingScreen)) {
            setLocale(mAppPreferences.getAppLanguage());
            AppsterChatManger.getInstance(this).reconnectIfNeed();
            // update connection
            SetLocal.setLocale(getApplicationContext());

            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            if (isHandleIntentNavigate) {
                intent.putExtra(ConstantBundleKey.BUNDLE_IS_LINK_PARAMETER, goingScreen);
                intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, postDetailID);
                intent.putExtra(ConstantBundleKey.BUNDLE_USER_PROFILE_DETAIL, userID);
                intent.putExtra(ConstantBundleKey.BUNDLE_STREAM_DETAIL_PLAY_URL, StreamURL);
                intent.putExtra(ConstantBundleKey.BUNDLE_STREAM_DETAIL_SLUG, Slug);
                intent.putExtra(ConstantBundleKey.BUNDLE_STREAM_IS_RECORDED, isRecorded);
            }
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(SplashScreenActivity.this,
                    R.anim.push_in_to_right, R.anim.push_in_to_left);
            SplashScreenActivity.this.startActivity(intent, options.toBundle());

            //
            // track app Launching success with userID
            EventTracker.setUID(mAppPreferences.getUserId());
            EventTracker.trackAppAccess();
            //
            finish();

        } else {
            checkReferralId();
        }
    }

    private void gotoLogInScreen() {
        setLocale(Constants.APP_LANGUAGE_ENGLISH_EN);
        mAppPreferences.setLanguageType(Constants.APP_LANGUAGE_TYPE_ENGLISH);
        mAppPreferences.setAppLanguage(Constants.APP_LANGUAGE_ENGLISH_EN);
//        mAppPreferences.setIsFirstGotoApp(true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(SplashScreenActivity.this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        ActivityCompat.startActivity(SplashScreenActivity.this, intent, options.toBundle());
        // track app Launching success
        EventTracker.trackAppAccess();
        //
        finish();
    }

    public void setLocale(String lang) {
        AppLanguage.setLocale(this, lang);
    }


    @Override
    public void onDestroy() {
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        mCompositeSubscription = null;
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (shouldGoToNextPage) {
            finish();
        }
    }

    private boolean handleIntentNavigate() {
        final Intent intent = getIntent();
        String scheme = intent.getScheme();
        if (scheme != null) {
            final Uri myURI = intent.getData();

            goingScreen = myURI.getQueryParameter("type");
            postDetailID = myURI.getQueryParameter("postId");
            Slug = myURI.getQueryParameter("slug");
            StreamURL = myURI.getQueryParameter("playUrl");
            userID = myURI.getQueryParameter("userId");
            isRecorded = "1".equals(myURI.getQueryParameter("isRecorded"));

            LogUtils.logE("StreamURL", StreamURL);
            LogUtils.logE("Slug", Slug);
            LogUtils.logE("userID", userID);
            LogUtils.logE("isRecorded", String.valueOf(isRecorded));
            LogUtils.logE("goingScreen", goingScreen);

            if (!StringUtils.isNullOrEmpty(goingScreen)) {
                return true;
            }
        }

        return false;
    }

}