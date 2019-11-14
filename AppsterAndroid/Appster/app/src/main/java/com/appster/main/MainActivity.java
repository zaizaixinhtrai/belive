package com.appster.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.PostDetailActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.bundle.BundleMedia;
import com.appster.data.AppPreferences;
import com.appster.features.blocked_screen.BlockedUserActivity;
import com.appster.features.home.BeLiveHomeScreenFragment;
import com.appster.features.home.HomeNavigation;
import com.appster.features.points.PointsFragment;
import com.appster.features.searchScreen.FragmentSearch;
import com.appster.features.searchScreen.viewholders.SearchListener;
import com.appster.features.stream.StreamingActivityGLPlus;
import com.appster.fragment.WallFeedFragment;
import com.appster.interfaces.LeftMenuAction;
import com.appster.interfaces.OnRefreshWhenEditPost;
import com.appster.interfaces.UpdateableFragment;
import com.appster.location.GPSTClass;
import com.appster.manager.AppLanguage;
import com.appster.manager.AppsterChatManger;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewCommentEventModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.NotificationModel;
import com.appster.models.ReportEvent;
import com.appster.models.UpdateLanguage;
import com.appster.models.UpdatePost;
import com.appster.models.event_bus_models.EventBusPushNotification;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.models.event_bus_models.EventBusRefreshHomeTab;
import com.appster.models.event_bus_models.EventBusRefreshSearchTab;
import com.appster.models.event_bus_models.EventBusRefreshWallfeedTab;
import com.appster.post.ActivityPostMedia;
import com.appster.services.GCMIntentServices;
import com.appster.services.RefreshFollowerListService;
import com.appster.tracking.EventTracker;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.FollowRequestModel;
import com.appster.webservice.request_models.UpdateLocationRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.AnimatorUtils;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CountryCode;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;
import com.data.room.FollowingLocalDbRxHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ogaclejapan.arclayout.ArcLayout;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.spacenavigationview.SpaceItem;
import com.spacenavigationview.SpaceNavigationView;
import com.spacenavigationview.SpaceOnClickListener;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.Bind;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;
import static com.appster.services.RefreshFollowerListService.LAST_TIME_SYNC_FOLLOWER_LIST;
import static com.apster.common.Constants.NOTIFYCATION_TYPE_MESSAGE;
import static com.apster.common.Constants.REQUEST_CATEGORY_DETAIL_ACTIVITY;
import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;

/**
 * Created by User on 14/7/2015.
 */
public class MainActivity extends BaseToolBarActivity implements
        View.OnClickListener, MainContract.MainView, SearchListener,
        HasSupportFragmentInjector, LeftMenuAction {

    @Bind(R.id.pager)
    ViewPager viewPager;

    @Bind(R.id.snvBottomBar)
    SpaceNavigationView snvBottomBar;
    private TabsPagerAdapter mAdapter;
    private int currentTabId = 0;

    @Bind(R.id.arc_layout)
    ArcLayout arcLayout;
    @Bind(R.id.menu_layout)
    View menuLayout;

    public BeLiveHomeScreenFragment fragmentHome;
    private PointsFragment mFragmentPoints;
    public FragmentSearch mFragmentSearchNew;
    public WallFeedFragment fragmentNewsfeed;

    private boolean doubleBackToExitPressedOnce = false;
    private final int TIME_INTERVAL = 2000;

    List<Animator> animList = new ArrayList<>();
    AnimatorSet animSet = null;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    private String idCurrentTabar = "idCurrentTabar";
    boolean needReconnectIfViaPushNotification = false;

    @Inject
    MainContract.MainActions mainPresenter;

    @Inject
    FollowingLocalDbRxHelper mFollowingLocalDbRxHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);

        AppLanguage.setLocale(this, mAppPreferences.getAppLanguage());
        showTextForNewlyUser();

        getToolbar().setContentInsetsAbsolute(0, 0);
        if (savedInstanceState != null) {
            Uri uriTemp = savedInstanceState.getParcelable("uriMedia");

            if (uriTemp != null) {
                fileUri = uriTemp;
            }

        }
        snvBottomBar.initWithSaveInstanceState(savedInstanceState);
        snvBottomBar.addSpaceItem(HomeNavigation.HOME, new SpaceItem(getString(R.string.bottom_bar_home), mBeLiveThemeHelper.getNavHomeIcon()));
        snvBottomBar.addSpaceItem(HomeNavigation.SEARCH, new SpaceItem(getString(R.string.bottom_bar_search), mBeLiveThemeHelper.getNavSearchIcon()));
        snvBottomBar.addSpaceItem(HomeNavigation.WALL_FEED, new SpaceItem(getString(R.string.bottom_bar_newfeeds), mBeLiveThemeHelper.getNavNewFeedsIcon()));
        snvBottomBar.addSpaceItem(HomeNavigation.POINTS, new SpaceItem(getString(R.string.bottom_bar_points), mBeLiveThemeHelper.getNavPointsIcon()));
        snvBottomBar.shouldShowFullBadgeText(true);
        snvBottomBar.showIconOnly();
        snvBottomBar.setCentreButtonIconColorFilterEnabled(false);
        setupClickBottomBar();
        if (savedInstanceState != null) {
            currentTabId = savedInstanceState.getInt(idCurrentTabar);
            // Hide ArcMenu
            hideMenu();
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mAppPreferences.isUserLogin() && checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, GCMIntentServices.class);
            startService(intent);
            // Track event user login and start app
            EventTracker.setUID(mAppPreferences.getUserId());
        }

        updateLocation();

//        mCompositeSubscription.add(mDailyBonusShowedUseCase.execute(null)
//                .flatMap(show -> mClaimedTreatUseCase.execute(null))
//                .subscribe(treatItemModel -> {
//                    if (treatItemModel == null) {
//                        DailyTreatMachineDialog dialog = DailyTreatMachineDialog.newInstance();
//                        dialog.show(getSupportFragmentManager(), DailyTreatMachineDialog.class.getName());
//                    }
//                }, Timber::e));

//        DailyBonusUtils.setupDailyBonusNotification(Integer.parseInt(mAppPreferences.getUserId()), 60, DailyBonusJobCreator.DAILY);
//        DailyBonusUtils.setupDailyBonusNotification(Integer.parseInt(mAppPreferences.getUserId()), 30, DailyBonusJobCreator.WEEKLY);


        setLeftMenuAction(this);

        restoreFragmentsIfNeeded(savedInstanceState);
    }

    private void showTextForNewlyUser() {
        if (getIntent().getExtras() != null) {
            boolean isNewlyUser = getIntent().getExtras().getBoolean(ConstantBundleKey.BUNDLE_TEXT_FOR_NEWLY_USER);
            if (isNewlyUser) {
                mAppPreferences.setFlagNewlyUser(true);
                DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
                builder.title(getString(R.string.newly_registered_user_title))
                        .message(getString(R.string.newly_registered_user))
                        .confirmText(getString(R.string.btn_text_ok))
                        .singleAction(true)
                        .build().show(this);
            }
        }
    }

    private void updateLocation() {

        if (!CheckNetwork.isNetworkAvailable(this))
            return;

        double latitude = 0;
        double longitude = 0;
        final GPSTClass gpstClass = GPSTClass.getInstance();

        // check if GPS enabled
        if (gpstClass.canGetLocation()) {

            latitude = gpstClass.getLatitude();
            longitude = gpstClass.getLongitude();

            UpdateLocationRequestModel requestModel = new UpdateLocationRequestModel(latitude, longitude);

            mCompositeSubscription.add(AppsterWebServices.get().updateLocation("Bearer " + mAppPreferences.getUserToken(), requestModel)
                    .subscribe(updateLocationDataResponse -> {

                    }, error -> Timber.e(error.getMessage())));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(idCurrentTabar, currentTabId);
        super.onSaveInstanceState(outState);
        snvBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentTabId = -1;
        snvBottomBar.setIsCenterButtonSelected(false);
    }

    //    public void onEventMainThread(EventBusPushNotification event) {
//        if (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_MESSAGE) {
////            handleNotificationMessage(event.getUnreadNumber());
//        } else {
//            if (isInFront) handlePushNotificationData(event);
//        }
//
//    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.e("onNewIntent");
        if (intent != null && intent.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
            refreshWholeApp();
        } else {
            notificationSwitchTo(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public int getCurrentTabPosition() {
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }

    public void handleNotificationMessage() {

//        final int unreadMessage = AppsterApplication.mAppPreferences.getNumberUnreadMessage();
//        Timber.e("unreadMessage = " + unreadMessage);
//        if (snvBottomBar != null) {
//            snvBottomBar.postDelayed(() -> {
//                if (snvBottomBar != null)
//                    if (unreadMessage == 0) {
//                        snvBottomBar.hideBadgeAtIndex(HomeNavigation.POINTS);
//                    } else {
//                        snvBottomBar.showBadgeAtIndex(HomeNavigation.POINTS, unreadMessage, ContextCompat.getColor(this, R.color.color_ff5167));
//                    }
//            }, 500);
//        }
        //TODO fragme
//        if (mFragmentMe != null) {
//            mFragmentMe.handleNotificationMessage();
//        }

    }

    public void handleShowWallfeedNotificationRed() {
        final boolean isShowing = AppsterApplication.mAppPreferences.getIsNewPostFromFollowingUsers();
        Timber.e("isShowing = " + isShowing);
        if (snvBottomBar != null) {
            snvBottomBar.postDelayed(() -> {
                if (snvBottomBar != null)
                    if (isShowing) {
                        snvBottomBar.showBadgeAtIndex(HomeNavigation.WALL_FEED, 0, ContextCompat.getColor(this, R.color.color_ff5167));
                    } else {
                        snvBottomBar.hideBadgeAtIndex(HomeNavigation.WALL_FEED);
                    }
            }, 500);
        }

    }

    @Override
    public void onEventMainThread(EventBusPushNotification event) {
        super.onEventMainThread(event);
        if (isInFront && event.getNotificationType() == NOTIFYCATION_TYPE_MESSAGE) {
            AppsterApplication.mAppPreferences.setNumberUnreadMessage(1);
//            handleNotificationMessage();
        } else if (isInFront && (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEWPOST
                || event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEW_RECORD)) {
            Timber.e("TYPE_NEWPOST =" + event.getNotificationType());
            handleShowWallfeedNotificationRed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        // Track App Exit
        EventTracker.trackExitAppWithoutRegister();
        DialogManager.getInstance().dismisDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLanguage.setLocale(this, mAppPreferences.getAppLanguage());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        if (!mIsCheckingUnreadMessage.get()) {
//            handleUnreadMessageCount();
//        }
        mAppPreferences.setCurrentStateMenuItem(0);
        if (mAppPreferences.isUserLogin()) {
            goneNotify(false);
            handleNewPushNotification(0);
        } else {
            goneNotify(true);
        }
        if (RefreshFollowerListService.shouldRefreshFollowerList()) {
            refreshFollowerList();
        }
        handleShowWallfeedNotificationRed();
        mainPresenter.checkHasLiveVideo();
        mainPresenter.loadDailyBonusCountDown();

    }

    @Override
    protected void onStop() {
        super.onStop();
        handleStopVideosPlayer();
    }

    private void handleUnreadMessageCount() {
        Timber.e("handleUnreadMessageCount");
        if (mAppPreferences.isUserLogin()) {
            handleNotificationMessage();
        }
    }

    @Override
    public int getLayoutContentId() {
        if (AppsterApplication.mAppPreferences.getUserCountryCode().equalsIgnoreCase(CountryCode.CHINA))
            return R.layout.main_activity_no_stream;
        return R.layout.main_activity;
    }

    @Override
    public void init() {
        intId();
//        setToolbarColor("#F5F5F5");
        setTxtTitleAsAppName();

    }

//    public static AppsterFragmentManager getAppsterFragmentManager() {
//        return appsterFragmentManager;
//    }

    private void intId() {
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        currentTabId = HomeNavigation.HOME;
        updateTopPadding(0);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAppPreferences.setCurrentTagOnHome(Constants.ID_FOR_SEARCH_FRAGMENT);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        menuLayout.setOnClickListener(this);

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }

//        initFragment();
        notificationSwitchTo(getIntent());
        setFontTitleToolbar(getString(R.string.font_futuracondensedmedium));

        if (!needReconnectIfViaPushNotification) {
            // Connect XMPP
            AppsterChatManger.getInstance(this).reconnectIfNeed();
            needReconnectIfViaPushNotification = false;
        }
    }

    void updateTopPadding(int topPadding) {
        if (contentLayout != null && contentLayout.getPaddingTop() != topPadding) {
            contentLayout.setPadding(0, topPadding, 0, 0);
        }
    }

    /**
     * Previous added fragment in the View pager will be restored by default but the settings are not. We need to restore it here
     */
    private void restoreFragmentsIfNeeded(Bundle savedBundle) {
        if (savedBundle == null) {
            return;
        }
        int paddingTop = 0;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof FragmentSearch && fragment.isAdded()) {
                    mFragmentSearchNew = (FragmentSearch) fragment;
                    mFragmentSearchNew.setSearchListener(this);
                }
            }
        }
        viewPager.post(() -> {
            // It makes sense to use the currentTabId here, but since the current tab id might be -1 when the menu is shown,
            // so I use the view pager index to restore the data
            if(viewPager!=null) {
                int currentIndex = viewPager.getCurrentItem();
                // since there is a lot of things to restore (data & ui), the simplest way is to simulate a click to the current tab item
                handleClickBottomBar(currentIndex, "");
            }
        });
    }

    //#region click events =========================================================================
    void onHomeTabClicked() {
        updateTopPadding(0);
        // Hide ArcMenu
        if (currentTabId == HomeNavigation.HOME) {
            if (fragmentHome != null) {
                fragmentHome.refreshToHotTab();
            }

        } else {
            hideMenu();
            disableSecondaryBadgeView();
            viewPager.setCurrentItem(HomeNavigation.HOME, false);
            currentTabId = HomeNavigation.HOME;
            if (fragmentHome != null) {
                fragmentHome.refreshHome();
            }
        }
    }

    void onNewFeedsTabClicked() {
        // Hide ArcMenu
        updateTopPadding((int) getResources().getDimension(R.dimen.height_top_bar));
        hideMenu();
        disableSecondaryBadgeView();
        viewPager.setCurrentItem(HomeNavigation.WALL_FEED, false);
        currentTabId = HomeNavigation.WALL_FEED;
        if (fragmentHome != null) fragmentHome.cancelCheckStatusTimer();
    }

    void onActionButtonClicked() {
        if (!snvBottomBar.isCenterButtonSelected() && currentTabId == HomeNavigation.ACTIONS) {
            hideMenu();
        } else {
            showMenu();
            currentTabId = HomeNavigation.ACTIONS;
        }
    }

    void onSearchTabClicked() {
        // Hide ArcMenu
        updateTopPadding((int) getResources().getDimension(R.dimen.height_top_bar));
        hideMenu();
        disableSecondaryBadgeView();
        viewPager.setCurrentItem(HomeNavigation.SEARCH, false);
        currentTabId = HomeNavigation.SEARCH;
        mainPresenter.checkHasLiveVideo();
        if (fragmentHome != null) fragmentHome.cancelCheckStatusTimer();
    }

    void onPointsClicked() {
        if (currentTabId != HomeNavigation.POINTS && AppsterApplication.mAppPreferences.getUserModel() != null)
            EventTracker.trackPointsTab(AppsterApplication.mAppPreferences.getUserModel().getUserId());
        updateTopPadding((int) getResources().getDimension(R.dimen.height_top_bar));
        hideMenu();
        enableSecondaryBadgeView();
        viewPager.setCurrentItem(HomeNavigation.POINTS, false);
        currentTabId = HomeNavigation.POINTS;
        if (fragmentHome != null) fragmentHome.cancelCheckStatusTimer();
    }

    //#endregion click events ======================================================================

    private void handleStopVideosPlayer() {
        if (fragmentNewsfeed != null) {
//            fragmentNewsfeed.stopVideos();
        }
    }

    @Override
    public void onVisibleLiveIcon(boolean isVisible) {
        snvBottomBar.addLiveSpaceItem(HomeNavigation.SEARCH, new SpaceItem(getString(R.string.bottom_bar_search),
                isVisible ? mBeLiveThemeHelper.getNavLiveIcon() : mBeLiveThemeHelper.getNavSearchIcon()));
        if (mFragmentSearchNew != null && mFragmentSearchNew.isFragmentUIActive()) {
            mFragmentSearchNew.getLivesInfo();
        }
    }

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
    public void hideProgress() {

    }

    @Override
    public void onRefreshSearch() {
        mainPresenter.checkHasLiveVideo();
    }

    @Override
    public void onVisiblePointsDot(boolean isVisible) {
        snvBottomBar.postDelayed(() -> {
            if (snvBottomBar != null)
                if (isVisible) {
                    snvBottomBar.showBadgeAtIndex(HomeNavigation.POINTS, 0, ContextCompat.getColor(this, R.color.color_ff5167));
                } else {
                    snvBottomBar.hideBadgeAtIndex(HomeNavigation.POINTS);
                }
        }, 500);
    }

    @Override
    public void onReceivePoints(@Nullable String message) {
        if (!StringUtil.isNullOrEmptyString(message))
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    class TabsPagerAdapter extends FragmentStatePagerAdapter {

        UpdateLanguage language;
        UpdatePost updatePost;
//        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public TabsPagerAdapter(FragmentManager fmg) {
            super(fmg);
        }

        public void update(UpdateLanguage language) {
            this.language = language;
            notifyDataSetChanged();
        }

        public void refreshPost(UpdatePost updatePost) {
            this.updatePost = updatePost;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).updateLanguage(language);
            }

            if (object instanceof OnRefreshWhenEditPost) {
                ((OnRefreshWhenEditPost) object).updatePost(updatePost);
            }

            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case HomeNavigation.HOME:
                    if (fragmentHome == null) {
                        fragmentHome = BeLiveHomeScreenFragment.newInstance();
                    }
                    return fragmentHome;

                case HomeNavigation.SEARCH:
                    if (mFragmentSearchNew == null) {
                        mFragmentSearchNew = FragmentSearch.newInstance();
                        mFragmentSearchNew.setSearchListener(MainActivity.this);
                    }
                    return mFragmentSearchNew;

                case HomeNavigation.WALL_FEED:
                    if (fragmentNewsfeed == null) {
                        fragmentNewsfeed = new WallFeedFragment();
                    }
                    return fragmentNewsfeed;

                case HomeNavigation.POINTS:
                    if (mFragmentPoints == null) {
                        mFragmentPoints = PointsFragment.newInstance();
                    }

                    return mFragmentPoints;
            }

            return null;
        }

//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            Fragment fragment = (Fragment) super.instantiateItem(container, position);
////            registeredFragments.put(position, fragment);
//            return fragment;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
////            registeredFragments.remove(position);
//            super.destroyItem(container, position, object);
//        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 4;
        }

    }

    private void setTitleTopBar(int index) {
        switch (index) {
            case HomeNavigation.HOME:
                setTxtTitleAsAppName();
                break;

            case HomeNavigation.SEARCH:
                setTxtTitleAsAppName();
                break;

            case HomeNavigation.WALL_FEED:
                setTxtTitleAsAppName();
                break;

            case HomeNavigation.POINTS:
                mFragmentPoints.setUserVisibleHint(true);

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_arc_comment:

                // Hide ArcMenu
                hideMenu();
                if (isMaintenance()) return;
                if (!mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ActivityPostMedia.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, CommonDefine.TYPE_QUOTES);
                startActivityForResult(intent, Constants.POST_REQUEST);
                break;

            case R.id.btn_arc_image:
                // Hide ArcMenu
                hideMenu();
                if (isMaintenance()) return;
                if (!mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                    return;
                }
                // Start camera
                showPicPopUp();
                break;

            case R.id.btn_arc_video:
                // Hide ArcMenu
                hideMenu();
                if (isMaintenance()) return;
                if (!mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                    return;
                }
                // Start camera
                // getVideoCamera();
                showVideosPopUp();
                break;

            case R.id.btn_arc_onair:

                // Hide ArcMenu
                if (!CheckNetwork.isNetworkAvailable(this)) {
                    utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this);
                    return;
                }
                hideMenu();
                if (isMaintenance()) return;
                if (!mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                    return;
                }
                startStream();
                break;

            case R.id.menu_layout:
                hideMenu();
                currentTabId = -1;
                break;
        }
    }


    private void startStream() {
        // china
        if (AppPreferences.getInstance(this).getUserCountryCode().equals(CountryCode.CHINA)) {
            return;
        }
        Intent intent = new Intent(this, StreamingActivityGLPlus.class);
        startActivityForResult(intent, Constants.LIVE_STREAM_REQUEST);

    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        if (animList.size() == 0) {
            for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
                animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
            }
        }
        if (animSet == null) {
            animSet = new AnimatorSet();
            animSet.setDuration(400);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(animList);
        }

        animSet.cancel();
        animSet.start();
    }


    @SuppressWarnings("NewApi")
    void hideMenu() {
        removeAllBackStackt();
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        final AnimatorSet animSetHide = new AnimatorSet();
        animSetHide.setDuration(400);
        animSetHide.setInterpolator(new AnticipateInterpolator());
        animSetHide.playTogether(animList);
        animSetHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animSet != null && !animSet.isRunning()) {
                    menuLayout.setVisibility(View.INVISIBLE);
//                    mBtnComposer.setImageResource(R.drawable.bottom_write);
//                    mBtnComposer.setSelected(false);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (animSet != null && animSet.isRunning()) {
                    animSetHide.cancel();
                }
            }
        });
        animSetHide.start();

    }

    private Animator createShowItemAnimator(View item) {
        if (snvBottomBar == null || viewPager == null) return null;
        float dx = (snvBottomBar.getRight() - Utils.dpToPx(56)) / 2 - item.getX();
        float dy = viewPager.getHeight() - item.getY();
        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        return ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );
    }

    private Animator createHideItemAnimator(final View item) {
        if (snvBottomBar == null || viewPager == null) return null;
        float dx = (snvBottomBar.getRight() - Utils.dpToPx(56)) / 2 - item.getX();
        float dy = viewPager.getHeight() - item.getY();
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case Constants.TWEET_SHARE_REQUEST_CODE:
                case Constants.REQUEST_CODE_SHARE_FEED:
                    mainPresenter.userEarnPoints(AppsterApplication.mAppPreferences.getShareStreamModel());
                    break;

                case Constants.REQUEST_PIC_FROM_LIBRARY:
                    Uri imageCroppedURI;
                    try {
                        imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
                    } catch (NullPointerException e) {
                        Timber.d(e);
                        return;
                    }
                    fileUri = data.getData();
                    if (fileUri == null) {
                        return;
                    }
                    performCrop(fileUri, imageCroppedURI);
                    break;

                case Constants.REQUEST_PIC_FROM_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        startPostWithMedia(resultUri, CommonDefine.TYPE_IMAGE);
                    } else {
                        Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.REQUEST_PIC_FROM_CAMERA:
                    fileUri = data.getData();
                    if (fileUri == null) {
                        return;
                    }
                    startPostWithMedia(fileUri, CommonDefine.TYPE_IMAGE);
                    break;
                case Constants.PICK_VIDEO_REQUEST:
                    fileUri = data.getData();
                    loadVideoAfterPickFromGallery(fileUri);
                    break;

                case Constants.RECORD_VIDEO_REQUEST:
                    fileUri = data.getData();
                    Timber.e("mRecordUrl " + fileUri);
                    startPostWithMedia(fileUri, CommonDefine.TYPE_VIDEO);
                    break;

                case Constants.VIDEO_TRIMMED_REQUEST:
                    String urlImage = data.getStringExtra(Constants.VIDEO_PATH);
                    Uri image = Uri.fromFile(new File(urlImage));

                    startPostWithMedia(image, CommonDefine.TYPE_VIDEO);
                    break;

                case Constants.COMMENT_REQUEST:

                    if (viewPager.getCurrentItem() == HomeNavigation.WALL_FEED || viewPager.getCurrentItem() == HomeNavigation.POINTS) {
                        if (fragmentNewsfeed != null) {
                            fragmentNewsfeed.onActivityResult(requestCode, resultCode, data);
                        }
                        //TODO fragme
//                        if (mFragmentMe != null) {
//                            mFragmentMe.onActivityResult(requestCode, resultCode, data);
//                        }
                    }
                    if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                        refreshWholeApp();
                    }

                    break;

                case Constants.REQUEST_LIKED_USERS_LIST_ACTIVITY:
                    if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                        refreshWholeApp();
                    }
                    break;

                case Constants.POST_REQUEST:
                    currentTabId = HomeNavigation.WALL_FEED;
                    if (fragmentNewsfeed != null) {
                        fragmentNewsfeed.onActivityResult(requestCode, resultCode, data);
                    }

                    if (viewPager.getCurrentItem() != HomeNavigation.WALL_FEED && snvBottomBar != null) {
                        snvBottomBar.changeCurrentItem(currentTabId);
                    }

                    break;

                case Constants.REQUEST_MENU:

                    if (mAppPreferences.getIsChangeLanguage()) {
                        setLocale();
                        mAdapter.update(new UpdateLanguage());

                        setTitleTopBar(viewPager.getCurrentItem());
                    }
                    break;

                case Constants.CONVERSATION_REQUEST:
                    getUnreadMessage();
                    break;

                case Constants.REQUEST_EDIT_POST:
                    if (viewPager.getCurrentItem() == HomeNavigation.POINTS || viewPager.getCurrentItem() == HomeNavigation.WALL_FEED) {
                        //TODO fragme
//                        if (mFragmentMe != null) {
//                            mFragmentMe.onActivityResult(requestCode, resultCode, data);
//                        }
                        if (fragmentNewsfeed != null) {
                            fragmentNewsfeed.onActivityResult(requestCode, resultCode, data);
                        }
                    } else if (viewPager.getCurrentItem() == HomeNavigation.SEARCH) {

                    }
                    break;

                case Constants.REQUEST_NOTIFICATION:

                    if (data != null) {
                        boolean isEditProfile = data.getBooleanExtra(ConstantBundleKey.BUNDLE_EDIT_ABLE_POST, true);
                        boolean viewMeProfile = data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, false);
                        if (viewMeProfile) {
                            //TODO fragme
//                            if (mFragmentMe == null) {
//                                onPointsClicked();
//                            } else {
//                                currentTabId = HomeNavigation.POINTS;
//                                if (snvBottomBar != null)
//                                    snvBottomBar.changeCurrentItem(currentTabId);
//                                mFragmentMe.refreshHeader(false);
//                                mFragmentMe.forceRefreshGift();
//                            }
                        }
                    }
                    break;

                case Constants.REQUEST_CODE_VIEW_POST_DETAIL:
                    if (viewPager.getCurrentItem() == HomeNavigation.POINTS) {
                        //TODO fragme
//                        if (mFragmentMe != null) {
//                            mFragmentMe.onActivityResult(requestCode, resultCode, data);
//                        }
                    }
                    break;

                case Constants.REQUEST_FOLLOW:

                    //TODO fragme
//                    if (viewPager.getCurrentItem() == HomeNavigation.POINTS && mFragmentMe != null) {
//                        mFragmentMe.onActivityResult(requestCode, resultCode, data);
//                    }

                    break;

                case Constants.REQUEST_CODE_EDIT_PROFILE:
                    //TODO fragme
//                    if (mFragmentMe != null) {
//                        mFragmentMe.onActivityResult(requestCode, resultCode, data);
//                    }

                    break;

                case Constants.SETTING_REQUEST:

                    if (mAppPreferences.getIsChangeLanguage()) {
                        setLocale();
                        mAdapter.update(new UpdateLanguage());

                        setTitleTopBar(viewPager.getCurrentItem());
                    }
                    if (data.getBooleanExtra(BlockedUserActivity.ARG_UNBLOCK_USER, false)) {
                        refreshWholeApp();
                    }
                    break;

                case Constants.REQUEST_CODE_VIEW_USER_PROFILE:

                    if (data != null) {
                        callChangeEventFromUserProfile(data);
                        if (data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_HOME, false)) {
                            onHomeTabClicked();
                            if (fragmentHome != null) fragmentHome.refreshToHotTab();
                            return;
                        }

                        if (data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                            refreshWholeApp();
                        }
                    }

                    break;

                case Constants.REQUEST_SEARCH_ACTIVITY:
                    if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                        refreshWholeApp();
                    }
                    break;

                case REQUEST_CATEGORY_DETAIL_ACTIVITY:
                case Constants.REQUEST_MEDIA_PLAYER_STREAM:
                    if (data == null)
                        return; /*only happen if user back by popup stream has been removed on MediaPlayerFragment*/
                    callChangeEventFollowUserLiveStream(data);
                    if (fragmentNewsfeed != null) {
                        fragmentNewsfeed.onActivityResult(requestCode, resultCode, data);
                    }

                    String userId = data.getStringExtra(Constants.USER_PROFILE_ID);
                    String userDisplayname = data.getStringExtra(Constants.USER_PROFILE_ID);

                    if (!StringUtil.isNullOrEmptyString(userId) &&
                            AppsterApplication.mAppPreferences.isUserLogin() &&
                            AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(userId)) {
                        // go to profile tab
                        currentTabId = HomeNavigation.POINTS;
                        if (snvBottomBar != null) snvBottomBar.changeCurrentItem(currentTabId);

                    } else {
                        if (!StringUtil.isNullOrEmptyString(userId)) {
                            startActivityProfile(userId, userDisplayname);
                        }
                    }

                    boolean goHome2 = data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_HOME, false);
                    if (goHome2) {
                        refreshHotTab();
                    }

                    boolean isViewerCloseStream = data.getBooleanExtra(ConstantBundleKey.BUNDLE_VIEWER_CLOSE_STREAM, false);
                    if (isViewerCloseStream) {
                        if (currentTabId == HomeNavigation.HOME) {
                            EventBus.getDefault().post(new EventBusRefreshHomeTab());
                        } else if (currentTabId == HomeNavigation.WALL_FEED) {
                            EventBus.getDefault().post(new EventBusRefreshWallfeedTab());
                        } else if (currentTabId == HomeNavigation.SEARCH) {
                            EventBus.getDefault().post(new EventBusRefreshSearchTab());
                        }
                    }

                    break;

                case Constants.LIVE_STREAM_REQUEST:

                    if (data != null) {
                        boolean showMeFrofile = data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, false);

                    } else {
                        onHomeTabClicked();
                        currentTabId = HomeNavigation.HOME;
                        if (snvBottomBar != null) snvBottomBar.changeCurrentItem(currentTabId);
                    }
                    if (fragmentHome != null) {
                        fragmentHome.refreshToHotTab();
                    }

                    break;
            }
        } else {
            if (requestCode == Constants.REQUEST_MEDIA_PLAYER_STREAM && mFragmentSearchNew != null) {
                mFragmentSearchNew.onActivityResult(requestCode, resultCode, data);
            }

            if (requestCode == Constants.REQUEST_CODE_SHARE_FEED)
                mainPresenter.userEarnPoints(AppsterApplication.mAppPreferences.getShareStreamModel());

        }

        if (mFragmentPoints != null)
            mFragmentPoints.onActivityResult(requestCode, resultCode, data);
        SocialManager.getInstance().onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void openPointsScreen() {
        onPointsClicked();
        snvBottomBar.changeCurrentItem(HomeNavigation.POINTS);
    }

    @Override
    public void startActivityProfile(String userID, String displayName) {
        if (mAppPreferences.getUserModel() != null && mAppPreferences.getUserModel().getUserId().equals(userID)) {
            onPointsClicked();
        } else {
            super.startActivityProfile(userID, displayName);
        }
    }

    private void refreshHotTab() {
        onHomeTabClicked();
        if (fragmentHome != null) {
            fragmentHome.refreshToHotTab();
        }
    }

    private void refreshWholeApp() {
        EventBus.getDefault().post(new EventBusRefreshFragment());
    }

    private void callChangeEventFollowUserLiveStream(Intent data) {

        if (data == null) {
            return;
        }

        FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);
        NewLikeEventModel likeEventModel = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_LIST_LIKE_FROM_PROFILE_ACTIVITY);

        ListenerEventModel listenerEventModel = new ListenerEventModel();
        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.NEW_EVENT_FROM_LIVE_STREAM);
        listenerEventModel.setFollowStatusChangedEvent(followStatusChangedEvent);
        listenerEventModel.setNewLikeEventModel(likeEventModel);

        eventChange(listenerEventModel);
    }

    private void callChangeEventFromUserProfile(Intent data) {

        if (data == null) {
            return;
        }

        ArrayList<NewCommentEventModel> arrNewCommentEvnt = data.getExtras().getParcelableArrayList
                (ConstantBundleKey.BUNDLE_DATA_LIST_COMMENT_FROM_PROFILE_ACTIVITY);

        ArrayList<NewLikeEventModel> arrNewLikeEvent = data.getExtras().getParcelableArrayList
                (ConstantBundleKey.BUNDLE_DATA_LIST_LIKE_FROM_PROFILE_ACTIVITY);
        ArrayList<ReportEvent> arrReport = data.getExtras().getParcelableArrayList
                (ConstantBundleKey.BUNDLE_DATA_REPORT_FROM_PROFILE_ACTIVITY);

        FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);

        ListenerEventModel listenerEventModel = new ListenerEventModel();
        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.NEW_EVENT_FROM_USER_POST_DETAIL);
        listenerEventModel.setArrNewLikeEvnt(arrNewLikeEvent);
        listenerEventModel.setArrNewCommentEvnt(arrNewCommentEvnt);
        listenerEventModel.setArrReportEvent(arrReport);
        listenerEventModel.setFollowStatusChangedEvent(followStatusChangedEvent);

        eventChange(listenerEventModel);
    }

    private void startPostWithImage(Uri fileUri) {
        Intent inten = new Intent(MainActivity.this, ActivityPostMedia.class);
        BundleMedia bundleMedia = new BundleMedia();

        bundleMedia.setIsPost(true);
        bundleMedia.setKey(ConstantBundleKey.BUNDLE_MEDIA_KEY);
        bundleMedia.setType(CommonDefine.TYPE_IMAGE);
        bundleMedia.setUriPath(fileUri.toString());
        bundleMedia.setIntent(inten);
        startActivityForResult(inten, Constants.POST_REQUEST);

    }

    private void startPostWithMedia(Uri fileUri, int Type) {
        Intent intent = new Intent(MainActivity.this, ActivityPostMedia.class);
        BundleMedia bundleMedia = new BundleMedia();

        bundleMedia.setIsPost(true);
        bundleMedia.setKey(ConstantBundleKey.BUNDLE_MEDIA_KEY);
        bundleMedia.setType(Type);
        bundleMedia.setUriPath(fileUri.toString());
        bundleMedia.setIntent(intent);
        startActivityForResult(intent, Constants.POST_REQUEST);
    }

    public void removeAllBackStackt() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void setupClickBottomBar() {

        snvBottomBar.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                onActionButtonClicked();
            }

            @Override
            public void onItemClick(@HomeNavigation int itemIndex, String itemName) {
                handleClickBottomBar(itemIndex, itemName);
            }

            @Override
            public void onItemReselected(@HomeNavigation int itemIndex, String itemName) {
                hideMenu();
                switch (itemIndex) {
                    case HomeNavigation.HOME:
//                        onHomeTabClicked();
                        if (fragmentHome != null) {
                            fragmentHome.onScrollUpListView();
                        }
                        break;
                    case HomeNavigation.POINTS:
                        //TODO fragme
//                        if (mFragmentMe != null) {
//                            mFragmentMe.onScrollUpListView();
//                        }
                        break;
                    case HomeNavigation.SEARCH:
                        if (mFragmentSearchNew != null) {
                            mFragmentSearchNew.onScrollUpListView();
                        }
                        break;
                    case HomeNavigation.WALL_FEED:
                        if (fragmentNewsfeed != null) {
                            fragmentNewsfeed.handleScrollAndLoadData();
                        }
                        break;
                    default:
                        break;
                }
                Timber.e("onItemReselected %d - %s", itemIndex, itemName);
                currentTabId = itemIndex;
            }
        });
//        handleStopVideosPlayer();
    }

    private void handleClickBottomBar(@HomeNavigation int itemIndex, String itemName) {
        switch (itemIndex) {
            case HomeNavigation.HOME:
                handleToolbar(true);
                hiddenImageSettingOnHome();
                onHomeTabClicked();
                defaultMenuIconOnHome();
                hideMenu();
                break;
            case HomeNavigation.WALL_FEED:
                defaultMenuIcon();
                handleToolbar(true);
                hiddenImageSetting();
                onNewFeedsTabClicked();
                break;
            case HomeNavigation.SEARCH:
                defaultMenuIcon();
                hiddenImageSetting();
                onSearchTabClicked();
                handleNewPushNotification(0);
                break;
            case HomeNavigation.POINTS:
                defaultMenuIcon();
//                handleNotificationMessage();
                hiddenImageSetting();
                onPointsClicked();
                break;
            default:
                break;
        }
    }

    void notificationSwitchTo(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            if (bundle.containsKey(ConstantBundleKey.BUNDLE_IS_LINK_PARAMETER)) {
                if (bundle.getString(ConstantBundleKey.BUNDLE_IS_LINK_PARAMETER).equalsIgnoreCase(Constants.TYPES_SCHEME_SCREEN.SCHEME_POST)) {
                    Intent intentDetail = new Intent(mActivity, PostDetailActivity.class);
                    intentDetail.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, bundle.getString(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID));
                    intentDetail.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, "");
                    mActivity.startActivityForResult(intentDetail, Constants.REQUEST_VIEW_NOTIFY, options.toBundle());
                    return;
                } else if (bundle.getString(ConstantBundleKey.BUNDLE_IS_LINK_PARAMETER).equalsIgnoreCase(Constants.TYPES_SCHEME_SCREEN.SCHEME_STREAM)) {
                    openViewLiveStream(bundle.getString(ConstantBundleKey.BUNDLE_STREAM_DETAIL_PLAY_URL), bundle.getString(ConstantBundleKey.BUNDLE_STREAM_DETAIL_SLUG), bundle.getBoolean(ConstantBundleKey.BUNDLE_STREAM_IS_RECORDED));
                    return;
                } else if (bundle.getString(ConstantBundleKey.BUNDLE_IS_LINK_PARAMETER).equalsIgnoreCase(Constants.TYPES_SCHEME_SCREEN.SCHEME_USER_DETAIL)) {
                    startActivityProfile(bundle.getString(ConstantBundleKey.BUNDLE_USER_PROFILE_DETAIL), "");
                    return;
                }
            } else {
                NotificationModel.NotificationEntity notificationEntity = bundle.getParcelable(ConstantBundleKey.BUNDLE_NOTIFICATION_KEY);
                if (notificationEntity != null) {
                    redirectNotificationShowing(notificationEntity);
                    if (notificationEntity.getNotification_type() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
                        needReconnectIfViaPushNotification = true;
                    }
                }
            }
        }

    }

    public void showFullFragmentProfileScreen(Fragment fragment) {
        if (!mActivity.isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_in_to_right,
                    R.anim.push_in_to_left);
            transaction.addToBackStack(null);
            //   transaction.replace(R.id.fragment_for_bottom, fragment).commit();
        }
    }

    private void refreshFollowerList() {
        if (mAppPreferences.isUserLogin()) {
            Timber.e("refreshFollowerList");
            final String mUserId = mAppPreferences.getUserId();
            refreshFollowerList(mUserId, 0);
//            mCompositeSubscription.add(Observable.just(true).delay(2, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(aBoolean -> startService(RefreshFollowerListService.createIntent(this, mAppPreferences.getUserId())),Timber::e));

        }
    }

    private void refreshFollowerList(String profileId, final int offset) {
        FollowRequestModel request = new FollowRequestModel();
        request.setProfile_id(profileId);
        request.setNextId(offset);
        request.setLimit(Constants.PAGE_LIMITED_1000);
        mCompositeSubscription.add(AppsterWebServices.get().getFollowersUsers(AppsterUtility.getAuth(), request)
                .observeOn(Schedulers.newThread())
                .filter(followResponseModel -> followResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .map(BaseResponse::getData)
                .subscribe(followResponseModel -> {
                    Timber.d("refreshFollowerList response ok");
                    if(offset == 0) {
                        deleteAllFollower(mFollowingLocalDbRxHelper);
                    }
                    mFollowingLocalDbRxHelper.bulkInsert(followResponseModel.getResult()).subscribe();

                    if(!followResponseModel.isEnd()) {
                        int offset1 = followResponseModel.getNextId();
                        refreshFollowerList(profileId, offset1);
                    } else {
                        saveLastTimeSync(System.currentTimeMillis());
                    }
                }, this::handleError));

    }


    private void handleError(Throwable e) {
        Timber.e(e);
    }

    private void deleteAllFollower(FollowingLocalDbRxHelper helper) {
        helper.erase().subscribe();
        saveLastTimeSync(Integer.MIN_VALUE);
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_time_delete", new Date(System.currentTimeMillis()).toString());
        editor.apply();
    }

    private void saveLastTimeSync(long currentTime) {
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME_SYNC_FOLLOWER_LIST, currentTime);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (handleCloseOpenDraw()) {
            // Handle Open and close menu slider in base activity
        } else if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), getString(R.string.Please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, TIME_INTERVAL);
        }
    }

    public void changeTabWhenClickSearch() {
//        onSearchTabClicked();
        if (snvBottomBar != null) snvBottomBar.changeCurrentItem(HomeNavigation.HOME);
    }


    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {

        if (listenerEventModel == null) {
            return;
        }

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.DELETE_POST) {
//            if(viewPager.getCurrentItem()!= POSITION_ME_FRAGMENT){
            //TODO fragme
//            if (mFragmentMe != null) {
//                mFragmentMe.eventChange(listenerEventModel);
//            }
//            }
            if (viewPager.getCurrentItem() != HomeNavigation.WALL_FEED) {
                if (fragmentNewsfeed != null) {
                    fragmentNewsfeed.eventChange(listenerEventModel);
                }
            }

        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EDIT_PROFILE) {

            //TODO fragme
//            if (mFragmentMe != null) {
//                mFragmentMe.eventChange(listenerEventModel);
//            }
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_COMMENT) {

            if (listenerEventModel.getTypeFragment() == ListenerEventModel.TypeFragment.PROFILE_ME) {

            }

            if (listenerEventModel.getTypeFragment() == ListenerEventModel.TypeFragment.TRENDING_POST) {
                //TODO fragme
//                if (mFragmentMe != null) {
//                    mFragmentMe.eventChange(listenerEventModel);
//                }
            }
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_LIKE) {
            //TODO fragme
//            if (viewPager.getCurrentItem() != HomeNavigation.POINTS && mFragmentMe != null) {
//                mFragmentMe.eventChange(listenerEventModel);
//            }
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_EVENT_FROM_USER_POST_DETAIL
                || listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_EVENT_FROM_LIVE_STREAM
                || listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EVENT_VIEW_VIDEOS) {
            if (fragmentNewsfeed != null) {
                fragmentNewsfeed.eventChange(listenerEventModel);
            }
        }

    }

    private AtomicBoolean mIsCheckingUnreadMessage = new AtomicBoolean(false);

    private void getUnreadMessage() {
        mIsCheckingUnreadMessage.set(true);
        mCompositeSubscription.add(AppsterWebServices.get().getUnreadMessage("Bearer " + AppsterApplication.mAppPreferences.getUserToken())
                .subscribe(unreadMessage -> {
                    AppsterApplication.mAppPreferences.setNumberUnreadMessage(unreadMessage.getData());
                    handleUnreadMessageCount();
                    mIsCheckingUnreadMessage.set(false);
                }, error -> {
                    mIsCheckingUnreadMessage.set(false);
                }));
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}


