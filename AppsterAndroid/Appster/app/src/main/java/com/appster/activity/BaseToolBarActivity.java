package com.appster.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.customview.BadgeImageView;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.features.edit_profile.ActivityEditProfile;
import com.appster.features.home.dailybonus.treatmachine.DailyTreatMachineDialog;
import com.appster.features.home.dialog.DailyTreatRevealPrizeDialog;
import com.appster.features.income.IncomeActivity;
import com.appster.features.invite_friend.InviteFriendActivity;
import com.appster.features.login.LoginActivity;
import com.appster.features.notification.NotificationActivity;
import com.appster.features.prizeBag.PrizeBagActivity;
import com.appster.features.setting.SettingActivity;
import com.appster.interfaces.LeftMenuAction;
import com.appster.main.MainActivity;
import com.appster.models.UserModel;
import com.appster.models.event_bus_models.EventBusPushNotification;
import com.appster.pocket.ActivityPocket;
import com.appster.refill.ActivityRefill;
import com.appster.services.RefreshFollowerListService;
import com.appster.tracking.EventTracker;
import com.appster.trending.ActivityTrending;
import com.appster.utility.AppsterUtility;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.SocialManager;
import com.appster.utility.glide.BlurTransformation;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.appster.webservice.request_models.LogoutRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;
import com.apster.common.view.NotificationView;
import com.data.repository.DailyBonusDataRepository;
import com.data.repository.datasource.DailyBonusDataSource;
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource;
import com.domain.interactors.dailybonus.GetClaimedTreatUseCase;
import com.domain.interactors.dailybonus.GetDailyBonusShowedUseCase;
import com.domain.models.TreatCollectModel;
import com.domain.repository.DailyBonusRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Created by sonnguyen on 10/7/15.
 */

public abstract class BaseToolBarActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CustomFontTextView txtTitle;
    private Toolbar mToolbar;
    private View homeView;
    public Activity mActivity;
    ImageButton mIBtnLeftToolbar;
    NotificationView mNotificationView;
    private DrawerLayout mDrawer;
    NavigationView navigationView;

    // Handle Profile in Sliding Menu Screen
    TextView tvUserName;
    TextView tvDisplayName;
    CircleImageView imgProfile;
    ImageView imgBackgroundProfile;
    View headerLayout;
    private TextView tvStart;
    private TextView tvBean;
    private TextView tvPoints;

    Bitmap avatarBlur;

    private BadgeImageView imvNotyOrSetting;
    private BadgeImageView mIvSecodaryBadge;
    protected GetClaimedTreatUseCase mClaimedTreatUseCase;
    protected GetDailyBonusShowedUseCase mDailyBonusShowedUseCase;
    protected FrameLayout contentLayout;
    private LeftMenuAction mLeftMenuAction;

    private long mLastClickLeftMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tool_bar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            txtTitle = (CustomFontTextView) mToolbar.findViewById(R.id.txt_title_screen);
            mIBtnLeftToolbar = (ImageButton) mToolbar.findViewById(R.id.slider_menu);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getEventClickBack().setOnClickListener(v -> mDrawer.openDrawer(GravityCompat.START));
        }
        mActivity = this;
        mNotificationView = (NotificationView) findViewById(R.id.notificationView);
        imvNotyOrSetting = (BadgeImageView) findViewById(R.id.imvNotyOrSetting);
        mIvSecodaryBadge = (BadgeImageView) findViewById(R.id.iv_secondary_badge);
        contentLayout = findViewById(R.id.content);
        if (getLayoutContentId() != 0) {
            homeView = getLayoutInflater().inflate(getLayoutContentId(), contentLayout, false);
            contentLayout.addView(homeView);
            ButterKnife.bind(this);//bind the home view used in MainActivity
            init();
        }
        getScreenWidth();

        imvNotyOrSetting.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent1 = NotificationActivity.createIntent(mActivity);
            AppsterApplication.mAppPreferences.setNumberUnreadNotification(0);
            handleNewPushNotification(0);

            mActivity.startActivityForResult(intent1, Constants.REQUEST_NOTIFICATION, options.toBundle());
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mNotificationView.setOnClickListener(v -> {
            if (mNotificationView.getPushModel().getNotificationType() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
                // Start live stream
                openViewLiveStream(mNotificationView.getPushModel().getPlayUrl(), mNotificationView.getPushModel().getSlug(),
                        mNotificationView.getPushModel().getUserImage(), false);
            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        // Inflate the header view at runtime
        headerLayout = navigationView.inflateHeaderView(R.layout.view_profile_draw);
        imgBackgroundProfile = (ImageView) headerLayout.findViewById(R.id.bgProfile);
        imgProfile = (CircleImageView) headerLayout.findViewById(R.id.profile_image);
        tvDisplayName = (TextView) headerLayout.findViewById(R.id.display_name);
        tvUserName = (TextView) headerLayout.findViewById(R.id.user_name);

        navigationView.setItemIconTintList(null);

        imgBackgroundProfile.setOnClickListener(view -> {
            mDrawer.closeDrawers();
            openOwnerProfile();
        });

        addMenuActionLayout();
        setupDrawerContent(navigationView);
        initDailyBonusUseCase();
        setTextFontDrawerLayout();
        setStartAndBean();
        setDrawerToggle();
        setClickSecondaryBadge();
    }

    public void setClickSecondaryBadge() {
        mIvSecodaryBadge.setOnClickListener(view -> openPrizeBag());
    }

    public void openPrizeBag() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(BaseToolBarActivity.this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = PrizeBagActivity.createIntent(BaseToolBarActivity.this);
        ActivityCompat.startActivityForResult(this, intent, Constants.REQUEST_PRIZE_BAG, options.toBundle());

    }

    private void addMenuActionLayout() {
        MenuItem income = navigationView.getMenu().findItem(R.id.nav_income);
        View v = View.inflate(this, R.layout.menu_item_action_layout, null);
        tvStart = v.findViewById(R.id.tvStart);
        ImageView iconStart = v.findViewById(R.id.icon);
        iconStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_gift_price));
        income.setActionView(v);

        MenuItem refill = navigationView.getMenu().findItem(R.id.nav_Refill);
        View viewRefill = View.inflate(this, R.layout.menu_item_action_layout, null);
        tvBean = viewRefill.findViewById(R.id.tvStart);
        ImageView iconBean = viewRefill.findViewById(R.id.icon);
        iconBean.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.refill_gem_icon));
        refill.setActionView(viewRefill);

        MenuItem rePoints = navigationView.getMenu().findItem(R.id.nav_Points);
        View viewPoints = View.inflate(this, R.layout.menu_item_action_layout, null);
        tvPoints = viewPoints.findViewById(R.id.tvStart);
        ImageView iconPoints = viewPoints.findViewById(R.id.icon);
        iconPoints.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navi_icon_points_small));
        rePoints.setActionView(viewPoints);
    }

    private void initDailyBonusUseCase() {
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        DailyBonusDataSource appConfigDataSource = new CloudDailyBonusDataSource(AppsterWebServices.get(), AppsterUtility.getAuth());
        DailyBonusRepository repository = new DailyBonusDataRepository(appConfigDataSource);
        mClaimedTreatUseCase = new GetClaimedTreatUseCase(repository, uiThread, ioThread);
        mDailyBonusShowedUseCase = new GetDailyBonusShowedUseCase(repository, uiThread, ioThread);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Locale.setDefault(newConfig.locale);
        Configuration config = new Configuration();
        config.locale = newConfig.locale;
        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        recreate();
    }

    private void setDrawerToggle() {
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            private float last = 0;

            @Override
            public void onDrawerSlide(View arg0, float arg1) {

                boolean opening = arg1 > last;
                boolean closing = arg1 < last;

                if (opening) {
                    updateCreditsAfterReceiveGiftForLeftMenu();
//                    setStartAndBean();
                }

                last = arg1;
            }

            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerOpened(View arg0) {
            }

            @Override
            public void onDrawerClosed(View arg0) {
            }

        });
    }


    public void updateCreditsAfterReceiveGiftForLeftMenu() {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickLeftMenu < 1000)
            return;
        mLastClickLeftMenu = SystemClock.elapsedRealtime();

        if (!AppsterApplication.mAppPreferences.isUserLogin() || !CheckNetwork.isNetworkAvailable(this)) {
            return;
        }

        CreditsRequestModel request = new CreditsRequestModel();
        mCompositeSubscription.add(AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(creditsResponseModel -> {
                    if (creditsResponseModel == null) return;
                    if (creditsResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        AppsterApplication.mAppPreferences.getUserModel().setTotalGold(creditsResponseModel.getData().getTotal_gold());
                        AppsterApplication.mAppPreferences.getUserModel().setTotalBean(creditsResponseModel.getData().getTotal_bean());
                        AppsterApplication.mAppPreferences.getUserModel().setTotalGoldFans(creditsResponseModel.getData().getTotalGoldFans());
                        AppsterApplication.mAppPreferences.getUserModel().setPoints(creditsResponseModel.getData().totalPoint);
                        setStartAndBean();
                    } else {
                        handleError(creditsResponseModel.getMessage(), creditsResponseModel.getCode());
                    }
                }, error -> {
                    Timber.e(error);
                }));
    }

    void setStartAndBean() {
        if (tvStart != null && tvBean != null && tvPoints != null && AppsterApplication.mAppPreferences.getUserModel() !=null) {
            DecimalFormat myFormatter = new DecimalFormat("#,###");
            tvStart.setText(myFormatter.format(AppsterApplication.mAppPreferences.getUserModel().getTotalGold()).replace(".", ","));
            tvBean.setText(myFormatter.format(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()).replace(".", ","));
            tvPoints.setText(myFormatter.format(AppsterApplication.mAppPreferences.getUserModel().getPoints()).replace(".", ","));
        }
    }


    private void setTextFontDrawerLayout() {

        Menu m = navigationView.getMenu();
        if (m == null) return;
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, j);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi, i);
        }
    }

    private void applyFontToMenuItem(MenuItem mi, int stt) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/opensanssemibold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("sans-serif", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        if (stt == 2) {
            mNewTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.silder_color_change)), 0, mNewTitle.length(), 0);
        }
        mi.setTitle(mNewTitle);
    }


    @TargetApi(21)
    private void changeStatusbarColor() {
        Window window = mActivity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(mActivity, R.color.new_background_tapbar));
    }

    public void setTxtTitleAsAppName() {

//        txtTitle.setTextColor(Color.parseColor("#9B9B9B"));
//        SpannableString contentLine = new SpannableString(getString(R.string.title_app_line));
//        contentLine.setSpan(new UnderlineSpan(), 2, contentLine.length(), 0);
//        contentLine.setSpan(new ForegroundColorSpan(Color.parseColor("#D8D8D8")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        txtTitle.setText(contentLine);
        if (getCurrentTabPosition() == 3) {
            return;
        }
        txtTitle.setVisibility(View.GONE);
        findViewById(R.id.imv_title).setVisibility(View.VISIBLE);
    }

    public void removeToolbarTitle() {
        txtTitle.setVisibility(View.GONE);
        findViewById(R.id.imv_title).setVisibility(View.GONE);
    }

    public void setToolbarColor(String color) {
        mToolbar.setBackgroundColor(Color.parseColor(color));
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);

                    return false;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        mDrawer.closeDrawers();
        if (AppsterApplication.mAppPreferences.getCurrentStateMenuItem() == menuItem.getItemId()) {
            return;
        }
        if (gotoLoginScreenIfHasNotLoggedIn()) {
            return;
        }
        Intent intent = null;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(BaseToolBarActivity.this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        switch (menuItem.getItemId()) {
            case R.id.nav_leader_board:
                intent = new Intent(BaseToolBarActivity.this, ActivityTrending.class);


                break;

            case R.id.nav_invite_friend:
                intent = new Intent(BaseToolBarActivity.this, InviteFriendActivity.class);
//                intent = FriendSuggestionActivity.createIntent(BaseToolBarActivity.this, ARG_LOGIN_FACEBOOK);
//                intent = RegisterActivity.createIntent(BaseToolBarActivity.this, ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_FACEBOOK,"","","");
                break;


            case R.id.nav_setting:
                intent = new Intent(this, SettingActivity.class);
                ActivityCompat.startActivityForResult(this, intent, Constants.SETTING_REQUEST, options.toBundle());
                AppsterApplication.mAppPreferences.setCurrentStateMenuItem(menuItem.getItemId());
                return;

            case R.id.nav_Refill:
                intent = new Intent(BaseToolBarActivity.this, ActivityRefill.class);
                break;

            case R.id.nav_transaction:
                intent = new Intent(BaseToolBarActivity.this, ActivityPocket.class);
                break;

            case R.id.nav_logout:
                confirmLogout();
                break;

            case R.id.nav_income:
                intent = IncomeActivity.createIntent(this);
                break;
            case R.id.nav_daily_treats:
                onDailyTreatSelected();
                break;
            case R.id.nav_profile:
                openOwnerProfile();
                break;
            case R.id.nav_Points:
                if (mLeftMenuAction != null) {
                    mLeftMenuAction.openPointsScreen();
                    if (AppsterApplication.mAppPreferences.getUserModel() != null)
                        EventTracker.trackLeftMenuPointsTab(AppsterApplication.mAppPreferences.getUserModel().getUserId());
                }
                break;
        }
        if (intent != null) {
            AppsterApplication.mAppPreferences.setCurrentStateMenuItem(menuItem.getItemId());
            startActivity(intent, options.toBundle());
        }
    }

    public int getCurrentTabPosition() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.getCurrentActivityRunning(this).equals(MainActivity.class.getName())) {
            imvNotyOrSetting.setVisibility(View.VISIBLE);
            handleToolbar(true);
        }
        handleProfileMenuSliding();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        ButterKnife.unbind(this);

        if (avatarBlur != null && !avatarBlur.isRecycled()) {
            avatarBlur.recycle();
            avatarBlur = null;
        }
    }

    @Override
    public void finish() {
        DialogManager.getInstance().dismisDialog();
        super.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBusPushNotification event) {
        if (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEWPOST
                || event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEW_RECORD) {
            return;
        }
        if (isInFront) handlePushNotificationData(event);
    }

    public void setLeftMenuAction(LeftMenuAction mLeftMenuAction) {
        this.mLeftMenuAction = mLeftMenuAction;
    }

    private void openOwnerProfile() {
        startActivityProfile(AppsterApplication.mAppPreferences.getUserModel().getUserId(),
                AppsterApplication.mAppPreferences.getUserModel().getUserName(),
                AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
    }

    public void handleNewPushNotification(int unreadNumber) {
        int count = unreadNumber;
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            count = count + AppsterApplication.mAppPreferences.getNumberUnreadNotification();
        }
        if (count > 0 && getCurrentTabPosition() < 3 && AppsterApplication.getCurrentActivityRunning(this).equals(MainActivity.class.getName())) {
            imvNotyOrSetting.setShowBadge(true);

        } else {
            imvNotyOrSetting.setShowBadge(false);
        }
        Timber.e("unreadNumber %d", count);
        Timber.e("getCurrentTabPosition %d", getCurrentTabPosition());
    }

    public void handlePushNotificationData(EventBusPushNotification event) {
        handleNewPushNotification(event.getUnreadNumber());
//        AppsterApplication.mAppPreferences.setNumberUnreadNotification(event.getUnreadNumber());
        if (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
            mNotificationView.updateDataNotification(event.getPushNotificationModel());
            showAutoDismissNotification();
//            new ChatNotificationShowTask().execute(event.getPushNotificationModel());
        }
    }

    /**
     * Set color for tab primary
     */
//    public abstract void setStatusBarColor();

    /**
     * Return the id of the layout that is the content of your activity.
     *
     * @return the id of your layout.
     */
    public abstract int getLayoutContentId();

    /**
     * Perform you initialization here. This is called after on create.
     */
    public abstract void init();

    /**
     * Use this instead of findViewById. This method will search the layout resource file that you
     * provide in {@code getLayoutContentId}
     *
     * @param id the id of the view to search for.
     * @return the found view if it exists.
     */
    public View findViewInContentById(int id) {
        return homeView.findViewById(id);
    }

    /**
     * Returns the current toolbar.
     *
     * @return the current toolbar.
     */
    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    public void setTopBarTile(String title) {
        if (txtTitle != null) {
            txtTitle.setText(title);
//            txtTitle.setTextColor(Color.parseColor("#9b9b9b"));
            txtTitle.setVisibility(View.VISIBLE);
            findViewById(R.id.imv_title).setVisibility(View.GONE);
        }
    }

    public void setTopBarTileNoCap(String title) {
        if (txtTitle != null) {
            txtTitle.setText(title);
//            txtTitle.setTextColor(Color.parseColor("#9b9b9b"));
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setAllCaps(false);
            txtTitle.setTextDirection(View.TEXT_DIRECTION_LOCALE);
            findViewById(R.id.imv_title).setVisibility(View.GONE);
        }
    }

    public void setTopBarTitleWithFont(String title, String font, int textSize, String color) {
        if (txtTitle != null) {
            txtTitle.setText(title);
            txtTitle.setTextColor(Color.parseColor(color));
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setCustomFont(this, font);
            txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
            findViewById(R.id.imv_title).setVisibility(View.GONE);
        }
    }

    public void handleToolbar(boolean isVisible) {
        if (mToolbar != null) {
            this.mToolbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        if (AppsterApplication.mAppPreferences.getUserModel() != null &&
                !StringUtil.isNullOrEmptyString(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
            imvNotyOrSetting.setVisibility(View.VISIBLE);
            imvNotyOrSetting.setShowBadge(false);
        } else {
            imvNotyOrSetting.setVisibility(View.GONE);
            imvNotyOrSetting.setShowBadge(false);
        }
    }

    public void goneNotify(boolean isGone) {

        if (isGone) {
            imvNotyOrSetting.setVisibility(View.GONE);
            imvNotyOrSetting.setShowBadge(false);
        } else {
            imvNotyOrSetting.setVisibility(View.VISIBLE);
        }

    }


    public void useAppToolbarBackButton() {
        setLeftToolbarIcon(mBeLiveThemeHelper.getToolbarBackIcon());
    }

    public void setLeftToolbarIcon(@DrawableRes int resId) {
        mIBtnLeftToolbar.setImageResource(resId);
    }

    public View getEventClickBack() {
        return mIBtnLeftToolbar;
    }


    public void defaultMenuIcon() {
        Timber.e("defaultMenuIcon");
        mIBtnLeftToolbar.setImageResource(mBeLiveThemeHelper.getAppMenuIcon());
        getEventClickBack().setOnClickListener(v -> mDrawer.openDrawer(GravityCompat.START));
    }

    public void showFullFragmentScreen(Fragment fragment) {
        if (!mActivity.isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_in_to_right,
                    R.anim.push_in_to_left);
            transaction.addToBackStack(null);
            transaction.replace(R.id.content, fragment).commit();
        }
    }

    /**
     * @return true if user hasn't logged in yet. otherwise return false
     */
    protected boolean gotoLoginScreenIfHasNotLoggedIn() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            goingLoginScreen();
            return true;
        }
        return false;
    }

    public void goingLoginScreen() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.slide_in_up, R.anim.keep_view_animation);
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            SocialManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_LOGIN:
                    finish();
                    break;
                case Constants.REQUEST_CODE_LOGOUT:
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void showAutoDismissNotification() {
        if (mNotificationView != null) {
            mNotificationView.showAnimationThenAutoDismiss();
        }
    }


    public void setColorPrimary(int color) {

    }


    public void handleProfileMenuSliding() {
        Menu menu = navigationView.getMenu();
        MenuItem nav_itemLogout = menu.findItem(R.id.nav_logout);
        String menuLoginLogout;

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            tvUserName.setVisibility(View.VISIBLE);
            tvDisplayName.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.VISIBLE);
            imgBackgroundProfile.setVisibility(View.VISIBLE);
            imgProfile.setVisibility(View.VISIBLE);
            menuLoginLogout = getString(R.string.logout_slider);
            UserModel user = AppsterApplication.mAppPreferences.getUserModel();
            tvDisplayName.setText(user.getDisplayName());
            tvUserName.setText(String.format("@%s", user.getUserName()));
            ImageLoaderUtil.displayUserImage(this, user.getUserImage(), imgBackgroundProfile, Resources.getSystem().getDisplayMetrics().widthPixels, Utils.dpToPx(160),
                    new BlurTransformation(getApplicationContext()));
            ImageLoaderUtil.displayUserImage(this, user.getUserImage(), imgProfile);

        } else {
            menuLoginLogout = getString(R.string.login_slider);
            tvUserName.setVisibility(View.VISIBLE);
            tvDisplayName.setVisibility(View.VISIBLE);
            imgBackgroundProfile.setVisibility(View.GONE);
        }

        nav_itemLogout.setTitle(menuLoginLogout);
        applyFontToMenuItem(nav_itemLogout, -1);

    }

    public void handleTurnoffMenuSliding() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void handleTurnOnMenuSliding() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void confirmLogout() {
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.are_you_sure_you_want_to_logout))
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(false)
                .onConfirmClicked(() -> {
                    DialogManager.getInstance().showDialog(this, getString(R.string.connecting_msg));
                    if (CheckNetwork.isNetworkAvailable(BaseToolBarActivity.this)) {
                        logoutAppsterServer();
                    } else {
                        finish();
                    }
                    RefreshFollowerListService.clearLastTimeSync();
                })
                .build().show(this);

    }

    private void logoutAppsterServer() {
        LogoutRequestModel request = new LogoutRequestModel();
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            request.setDevice_token(AppsterApplication.mAppPreferences.getDevicesToken());
        }
        mCompositeSubscription.add(AppsterWebServices.get().logoutApp("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(logoutDataResponse -> {
                    AppsterApplication.logout(BaseToolBarActivity.this);
                    finish();
                }, error -> {
                    Timber.e(error.getMessage());
                    finish();
                }));
    }


    public void setImageEditProfile() {
        imvNotyOrSetting.setImageResource(mBeLiveThemeHelper.getAppEditProfileIcon());
        imvNotyOrSetting.setVisibility(View.VISIBLE);
        imvNotyOrSetting.setShowBadge(false);
        imvNotyOrSetting.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent1 = new Intent(mActivity, ActivityEditProfile.class);
            mActivity.startActivityForResult(intent1, Constants.REQUEST_CODE_EDIT_PROFILE, options.toBundle());
        });
    }

    public void setImageEditProfile(View.OnClickListener onClickListener) {
        imvNotyOrSetting.setImageResource(mBeLiveThemeHelper.getAppOptionMenuIcon());
        imvNotyOrSetting.setOnClickListener(onClickListener);
        imvNotyOrSetting.setShowBadge(false);
    }

    public void hiddenImageSetting() {
        imvNotyOrSetting.setImageResource(mBeLiveThemeHelper.getAppNotificationBellIcon());
        handleNotificationClick();
    }

    private void handleNotificationClick() {
        handleNewPushNotification(0);
        imvNotyOrSetting.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent1 = NotificationActivity.createIntent(mActivity);
            AppsterApplication.mAppPreferences.setNumberUnreadNotification(0);
            handleNewPushNotification(0);

            mActivity.startActivityForResult(intent1, Constants.REQUEST_NOTIFICATION, options.toBundle());
        });
    }

    public void hiddenImageSettingOnHome() {
        imvNotyOrSetting.setImageResource(mBeLiveThemeHelper.getAppNotificationBellIconLight());
        handleNotificationClick();
    }

    public void defaultMenuIconOnHome() {
        mIBtnLeftToolbar.setImageResource(mBeLiveThemeHelper.getAppMenuIconLight());
        getEventClickBack().setOnClickListener(v -> mDrawer.openDrawer(GravityCompat.START));
    }


    public void setFontTitleToolbar(String fontName) {

//        txtTitle.setTypeface(AssetsUtil.getFont(this, fontName));

    }

    public boolean handleCloseOpenDraw() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return true;
        }
        return false;
    }

    private void getScreenWidth() {
        if (AppsterApplication.mAppPreferences.getScreenWidth() <= 0) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            AppsterApplication.mAppPreferences.setScreenWidth(width);
        }

    }

    private void onDailyTreatSelected() {
        showDialog(this, getResources().getString(R.string.connecting_msg));
        mCompositeSubscription.add(mClaimedTreatUseCase.execute(null)
                .subscribe(treatItemModel -> {
                    if (treatItemModel == null) {
                        DailyTreatMachineDialog dialog = DailyTreatMachineDialog.newInstance();
                        dialog.show(getSupportFragmentManager(), DailyTreatMachineDialog.class.getName());
                    } else {
                        DailyTreatRevealPrizeDialog dialog = DailyTreatRevealPrizeDialog.newInstance(new TreatCollectModel(treatItemModel.treatId,
                                treatItemModel.title == null ? "" : treatItemModel.title,
                                treatItemModel.prizeDesc == null ? "" : treatItemModel.prizeDesc,
                                treatItemModel.prizeImgUrl == null ? "" : treatItemModel.prizeImgUrl,
                                treatItemModel.prizeAmount,
                                treatItemModel.prizeRank,
                                true));
                        dialog.show(getSupportFragmentManager(), DailyTreatRevealPrizeDialog.class.getName());
                    }
                    dismisDialog();
                }, e -> {
                    String message = getString(R.string.network_unstable_error_message);
                    String okButton = getString(R.string.btn_text_ok);
                    String title = getString(R.string.error_title);
                    DialogUtil.showConfirmDialogSingleAction(this, title, message, okButton, null);
                    dismisDialog();
                }));
    }

    /**
     * Get the secondary badge view. You might want to enable the badge view before apply some info to it
     *
     * @return the current secondary badge view
     */
    public BadgeImageView getSecondaryBadgeView() {
        return mIvSecodaryBadge;
    }

    /**
     * Enable the secondary badge. After this call, the view is visible with the previous info (except the listener) and enabled
     */
    public void enableSecondaryBadgeView() {
        if (mIvSecodaryBadge != null) {
            mIvSecodaryBadge.setEnabled(true);
            mIvSecodaryBadge.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Disable the secondary badge view. This view is now gone, not clickable and the previous click event will be set to null.
     * Next time, when you want to enable this view, remember to re-set your listener
     */
    public void disableSecondaryBadgeView() {
        if (mIvSecodaryBadge != null) {
            mIvSecodaryBadge.setEnabled(false);
            mIvSecodaryBadge.setVisibility(View.INVISIBLE);
        }
    }
}
