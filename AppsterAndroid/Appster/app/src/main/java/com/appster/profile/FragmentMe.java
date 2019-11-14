package com.appster.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.adapters.AdapterWallFeed;
import com.appster.comments.ItemClassComments;
import com.appster.customview.InterceptViewpager;
import com.appster.fragment.BaseVisibleItemFragment;
import com.appster.interfaces.UpdateableFragment;
import com.appster.main.MainActivity;
import com.appster.manager.AppLanguage;
import com.appster.manager.WallFeedManager;
import com.appster.models.DailyTopFanModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewCommentEventModel;
import com.appster.models.UpdateLanguage;
import com.appster.models.UserModel;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.models.event_bus_models.NewMessageEvent;
import com.appster.sendgift.GiftItemModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.UserProfileRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.UserProfileResponseModel;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.LogUtils;
import com.apster.common.UiUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.squareup.leakcanary.RefWatcher;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.appster.AppsterApplication.mAppPreferences;

/**
 * Created by User on 9/24/2015.
 */
public class FragmentMe extends BaseVisibleItemFragment implements AppBarLayout.OnOffsetChangedListener, UpdateableFragment,
        AdapterWallFeed.BlockCallback, LoadingDataListener {

    public static final String BUNDEL_USER_NAME = "user_name";
    public static final String BUNDEL_USER_ID = "user_id";
    public static final String BUNDEL_IS_TAB = "user_tab";
    public static final String BUNDEL_USER_DISPLAY_NAME = "user_display_name";
    public static final int TAB_LIST_INDEX = 1;
    public static final int TAB_GRID_INDEX = 0;
    public static final int TAB_GIFT_INDEX = 2;

    @Bind(R.id.framelayout_header_user_info)
    LinearLayout frameUserInfo;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @Bind(R.id.rl_live_notification)
    RelativeLayout rlLiveNotification;
    @Bind(R.id.vp_inner_content)
    InterceptViewpager viewPager;
    UserProfileView userProfileView;
    @Bind(R.id.swipeRefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    UserModel userProfileDetails;
    //    private PerformUserProfile performUserProfile;
    int userID;
    String mUserName;
    String currentUserID = "0"; // by default
    //    String displayName;
    private boolean isTab;
    boolean isAppOwner;
    View.OnClickListener mBackClick;
    boolean finishLoadData = false;
    boolean isLoading;
    boolean hasRefreshedFromActivityResult;
    private AtomicBoolean mIsShouldShowList = new AtomicBoolean(false);


    private int toolbarOffset = 0;
    private int toolbarHeight;
    boolean isNewPost = false;
    boolean isChangeProfileImage = false;


    private ViewPagerAdapter viewPagerAdapter;
    ListFragment listFragment;
    GridFragment gridFragment;
    GiftFragment giftFragment;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(metaData -> {

    });

    public static FragmentMe getInstance(String userID, String userName, boolean isTab, String displayName) {
        FragmentMe f = new FragmentMe();
        Bundle args = new Bundle();
        int id;
        try {
            id = Integer.parseInt(userID);
        } catch (NumberFormatException e) {
            id = 0;
        }
        args.putInt(BUNDEL_USER_ID, id);
        args.putString(BUNDEL_USER_NAME, userName);
        args.putBoolean(BUNDEL_IS_TAB, isTab);
        args.putString(BUNDEL_USER_DISPLAY_NAME, displayName);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getInt(BUNDEL_USER_ID);
            mUserName = bundle.getString(BUNDEL_USER_NAME, "");
            isTab = bundle.getBoolean(BUNDEL_IS_TAB);
        }
        if (mAppPreferences.getUserModel() != null) {
            isAppOwner = mAppPreferences.getUserModel().getUserId().equals(String.valueOf(userID));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (mRootView != null) {
            return mRootView;
        }
        mRootView = inflater.inflate(R.layout.fragment_profile_page_activity_new, container, false);
        ButterKnife.bind(this, mRootView);
        registerEventBus();
        if (mAppPreferences.isUserLogin()) {
            currentUserID = mAppPreferences.getUserModel().getUserId();
        }

//        displayName = getArguments() != null ? getArguments().getString(BUNDEL_USER_DISPLAY_NAME) : "";
//        performUserProfile = new PerformUserProfile(getActivity(), userID, mUserName);
        mBackClick = v -> getActivity().onBackPressed();
        handleMenuButton();

        // add header view
        addHeaderView();

        UiUtils.setColorSwipeRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        if (!isTab) {
            getHeaderData();
        }
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = AppsterApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
        unRegisterEventBus();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //region implemented methods ===================================================================
    @Override
    public void OnBlockSuccessfully() {
//        Toast.makeText(getContext(), getString(R.string.blocked), Toast.LENGTH_SHORT).show();
        if (isBlockCurrentStreamer()) {
            EventBus.getDefault().post(new EventBusRefreshFragment());
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
//            getActivity().setResult(RESULT_OK, intent);
//            getActivity().finish();

        } else {
            Intent intent = getActivity().getIntent();
            intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private boolean isBlockCurrentStreamer() {
        // TODO: 4/27/17 implement block streamer
        return AppsterUtility.readSharedSetting(getContext(), "current_viewing_stream", "0").equalsIgnoreCase(String.valueOf(userID));
    }
    //end region ===================================================================================

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void getUserProfile(boolean isShowingDialog) {
        if (!DialogManager.isShowing() && isShowingDialog) {
            DialogManager.getInstance().showDialog(getContext(), getResources().getString(R.string.connecting_msg));
        }

        final UserProfileRequestModel request = new UserProfileRequestModel();
        request.setUser_id(userID);
        request.setUserName(mUserName);
        mCompositeSubscription.add(AppsterWebServices.get().getUserProfile("Bearer " + mAppPreferences.getUserToken(), request)
                .subscribe(userProfileResponseModel -> {
                    if (DialogManager.isShowing()) {
                        DialogManager.getInstance().dismisDialog();
                    }
                    if (userProfileResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        onGetUserProfileError(userProfileResponseModel.getMessage(), userProfileResponseModel.getCode());
                        return;
                    }

                    onGetUserProfileSuccessfully(userProfileResponseModel, userProfileResponseModel.getData().isCanChangePassword(), request.getView(), isShowingDialog);
                }, error -> {
                    if (DialogManager.isShowing()) {
                        DialogManager.getInstance().dismisDialog();
                    }
                    onGetUserProfileError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));

    }

    private void onGetUserProfileSuccessfully(BaseResponse<UserProfileResponseModel> userProfileResponseModel, boolean canChangePassword, int view, boolean isShowingDialog) {
        if (isFragmentUIActive()) {

            swipeRefreshLayout.setRefreshing(false);
            isLoading = false;
            updateHeader(userProfileResponseModel, canChangePassword);
            finishLoadData = true;

            handleNotificationMessage();
        }
    }

    private void onGetUserProfileError(String message, int code) {
        if (isFragmentUIActive()) {
            swipeRefreshLayout.setRefreshing(false);
            isLoading = false;
            onErrorWebServiceCall(message, code);
        }
    }

    void updateHeader(BaseResponse<UserProfileResponseModel> userProfileResponseModel, boolean CanChangePassword) {

        userProfileDetails = userProfileResponseModel.getData().getUser();
//        userProfileDetails.setUserId(userID);
        // save user inform
        if (mAppPreferences.isUserLogin() && userProfileDetails.getUserId().equalsIgnoreCase(mAppPreferences.getUserModel().getUserId())) {
            mAppPreferences.saveUserInforModel(userProfileDetails);
        }

//        userProfileDetails = userProfileResponseModel.getData().getUser();
        userProfileView.setStreamDetail(userProfileResponseModel.getData().getStreamDetail());
        userProfileView.setUserProfileDetails(userProfileDetails, mBackClick, currentUserID);
        if (listFragment != null) {
            listFragment.setUserProfileDetails(userProfileDetails);
        }

        if (mAppPreferences.isUserLogin()) {

            if (isAppOwner) {

                mAppPreferences.getUserModel().setFollowingCount(userProfileResponseModel.getData().getUser().getFollowingCount());
                userProfileView.updateFollowCount();
            }
        }

        userProfileView.setIsStreaming(userProfileResponseModel.getData().isStreaming(), userProfileResponseModel.getData().getCurrentStream());
        setupViewPager();
    }

    public void refreshData() {
        isLoading = true;
        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            LogUtils.logE(FragmentMe.class.getName(), "refresh data");
            refreshHeader(false);
            refreshPost();
            forceRefreshGift();

        } else {
            ((BaseActivity) getActivity()).utility.showMessage(getString(R.string.app_name),
                    getString(R.string.no_internet_connection), getActivity());
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void forceRefreshGift() {
        if (giftFragment != null) {
            giftFragment.refresh();
        }
    }

    public void refreshHeader(boolean isShowingDialog) {
        getUserProfile(isShowingDialog);
    }

    private void refreshPost() {
        if (TAB_LIST_INDEX == viewPager.getCurrentItem()) {
            if (listFragment != null) {
                listFragment.refreshData();
            }

            if (isNewPost && gridFragment != null) {
                gridFragment.setNewPost(true);
            }

        } else if (TAB_GRID_INDEX == viewPager.getCurrentItem()) {
            if (gridFragment != null) {
                gridFragment.refreshData();
            }

            if (isNewPost && listFragment != null) {
                listFragment.setNewPost(true);
            }
        } else {
            if (listFragment != null && giftFragment != null) {
//                giftFragment.refresh();
                listFragment.setNewPost(true);
                gridFragment.setNewPost(true);
            }
        }
    }

    private void addHeaderView() {

        userProfileView = new UserProfileView(getActivity(), isAppOwner);
        userProfileView.setBlockCallback(this);
        userProfileView.setOnTabChange(new OnUserProfileChangeView() {
            @Override
            public void onChangeToListView() {
                if (isNewPost) {
                    refreshData();
                    isNewPost = false;
                }

                if (isChangeProfileImage) {
                    refreshData();
                    isChangeProfileImage = false;
                }
                viewPager.setCurrentItem(TAB_LIST_INDEX, true);
            }

            @Override
            public void onChangeToGridView() {
                viewPager.setCurrentItem(TAB_GRID_INDEX, true);

            }

            @Override
            public void onChangeToGift() {
                viewPager.setCurrentItem(TAB_GIFT_INDEX, true);
            }
        });

        userProfileView.setFollowUserListener(type -> {

            userProfileDetails.setIsFollow(type);
            FollowStatusChangedEvent event = new FollowStatusChangedEvent();
            event.setFollowType(type);
            event.setStream(false);
            event.setUserId(userProfileDetails.getUserId());
            WallFeedManager.getInstance().updateFollowStatus(event);

        });
        if (userProfileView.getParent() != null) {
            ((ViewGroup) (userProfileView.getParent())).removeView(userProfileView);
        }
        userProfileView.setRedNotification(rlLiveNotification);
        toolbarHeight = userProfileView.getHeight();
        frameUserInfo.addView(userProfileView);
    }

//    private RecyclerView.OnScrollListener scrollChangeListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (typeList == TypeList.LIST_PROFILE) {
//
//            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//
//        }
//    };


//    private void showContentListByType() {
////        listAdapter.setLoaded();
//        gridAdapter.setLoaded();
//        if (typeList == TypeList.LIST_PROFILE) {
////            listAdapter.setRecyclerView(recyclerView);
//            recyclerView.setAdapter(listAdapter);
//            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.removeItemDecoration(gridSpacingItemDecoration);
//            listAdapter.notifyAllSectionsDataSetChanged();
//
//        } else if (typeList == TypeList.GRID_PROFILE) {
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setLayoutManager(gridLayoutManager);
//            gridAdapter.setRecyclerView(recyclerView);
//            recyclerView.setAdapter(gridAdapter);
//            recyclerView.removeItemDecoration(gridSpacingItemDecoration);
//            recyclerView.addItemDecoration(gridSpacingItemDecoration);
//            gridAdapter.notifyDataSetChanged();
//        }
////        recyclerView.addOnScrollListener(scrollChangeListener);
//    }

    @Override
    public void updateLanguage(UpdateLanguage language) {

        // do whatever you want to update your UI
        AppLanguage.setLocale(getActivity(), mAppPreferences.getAppLanguage());
        addHeaderView();

        if (userProfileDetails != null && !StringUtil.isNullOrEmptyString(currentUserID)) {
            userProfileView.setUserProfileDetails(userProfileDetails, mBackClick, currentUserID);
            refreshData();
        }

    }

    private void handleMenuButton() {
        if (isTab) {
//            ((BaseToolBarActivity) getActivity()).defaultMenuIcon();
        } else {
            ((BaseToolBarActivity) getActivity()).useAppToolbarBackButton();
            ((BaseToolBarActivity) getActivity()).getEventClickBack().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded) {
            getHeaderData();

        }
        // handle the Header
        if (getUserVisibleHint()) {
            ((BaseToolBarActivity) getActivity()).setTopBarTile(getString(R.string.profile_header));
            ((BaseToolBarActivity) getActivity()).setImageEditProfile();

            if (listFragment != null) {
                listFragment.refreshRecyclerView();
            }
        } else {
            if (listFragment != null) {
                listFragment.handleStopVideosResetPlayer();
            }
        }

        if (mAppPreferences.isUserLogin()) {
            if (isAppOwner && _areLecturesLoaded && isVisibleToUser && finishLoadData) {

                if (userProfileView != null) {
                    userProfileView.updateFollowCount();
                    userProfileView.setCount(mAppPreferences.getUserModel());
                }
            }
        }


    }

    private void getHeaderData() {
        isLoading = true;
        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            getUserProfile(true);
            _areLecturesLoaded = true;
        } else {
            ((BaseActivity) getActivity()).utility.showMessage(getActivity().getString(R.string.app_name), getActivity().getString(R.string.no_internet_connection), getActivity());
            isLoading = false;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        swipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userProfileView != null && userProfileView.isStreaming() && !hasRefreshedFromActivityResult) {
            refreshData();
        }
        hasRefreshedFromActivityResult = false;
        appBarLayout.addOnOffsetChangedListener(this);
        BaseToolBarActivity activity = (BaseToolBarActivity) getActivity();
        if (activity instanceof UserProfileActivity ||
                (activity instanceof MainActivity && activity.getCurrentTabPosition() == 3)) {
            ((BaseToolBarActivity) getActivity()).setTopBarTile(getString(R.string.profile_header));
        }
        Timber.e("giftFragment=" + giftFragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hasRefreshedFromActivityResult = false;

        switch (requestCode) {
            case Constants.REQUEST_CODE_EDIT_PROFILE:
                appBarLayout.addOnOffsetChangedListener(this);
                callChangeEventProfile(data);
//                refreshPost();
                refreshData();
                break;

            case Constants.REQUEST_CODE_VIEW_POST_DETAIL:
                if (resultCode == RESULT_OK && gridFragment != null && listFragment != null) {
                    gridFragment.updateGrid(data);
                    listFragment.onGridTabUpdated(data);
                    FollowStatusChangedEvent followStatusChangedEvent = data.getExtras().getParcelable(ConstantBundleKey.BUNDLE_PROFILE_CHANGE_FOLLOW_USER);
                    if (followStatusChangedEvent != null) {
                        checkChangeFollow(followStatusChangedEvent);
                    }
                }
                break;

            case Constants.COMMENT_REQUEST:
                if (data != null && listFragment != null) {
                    listFragment.getDataFromCommentClass(data);
                }
                callChangeEventComment(data);
                break;

            case Constants.POST_REQUEST:
                break;

            case Constants.REQUEST_EDIT_POST:
                if (listFragment != null) {
                    listFragment.updateEditPost(data);
                }
                break;

            case Constants.REQUEST_FOLLOW:
                if (userProfileView != null) {
                    userProfileView.updateFollowCount();
                }
                break;

            case Constants.REQUEST_MEDIA_PLAYER_STREAM:
                refreshData();
                hasRefreshedFromActivityResult = true;
                break;

            case Constants.LIVE_STREAM_REQUEST:

                if (isAppOwner && mAppPreferences.isUserLogin()) {
                    if (userProfileView != null) {
                        userProfileView.setCount(mAppPreferences.getUserModel());
                    }
                }
                break;

            case Constants.REQUEST_TOPFAN_ACTIVITY:

                updateTotalGoldFans();
                break;

            case Constants.REQUEST_MESSAGE_LIST_ACTIVITY:
                updateTotalGoldFans();
                break;

            case Constants.CONVERSATION_REQUEST:
                updateTotalGoldFans();
                break;


        }

        if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
            refreshData();
            Intent intent = getActivity().getIntent();
            intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
            getActivity().setResult(RESULT_OK, intent);
        }
    }

    void checkChangeFollow(FollowStatusChangedEvent followStatusChangedEvent) {
        userProfileDetails.setIsFollow(followStatusChangedEvent.getFollowType());
        userProfileView.updateFollowButton(userProfileDetails.getIsFollow());
        userProfileView.updateUserProfileDetails(userProfileDetails);
    }

    void updateTotalGoldFans() {
        if (isAppOwner && mAppPreferences.isUserLogin()) {
            if (userProfileView != null) {
                userProfileView.setCount(mAppPreferences.getUserModel());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusRefreshFragment event) {
        Timber.e("EventBusRefreshFragment_Me");
        if (event.postType == CommonDefine.TYPE_QUOTES) {
            if (viewPager != null) {
                viewPager.setCurrentItem(TAB_LIST_INDEX);
                mIsShouldShowList.set(true);
            }
        }
        refreshData();
        AppsterApplication.mAppPreferences.setIsRefreshGridAndList(true);
        hasRefreshedFromActivityResult = true;
        isNewPost = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(NewMessageEvent event) {
        Timber.d("onEventBus NewMessageEvent");
        if (!event.getData().isGroupMessage()) {
//            AppsterChatManger.getInstance(getContext()).saveUserNameSendNewMessage(event.getData().getUser_name());
            AppsterApplication.mAppPreferences.setNumberUnreadMessage(1);
            handleNotificationMessage();
        }
    }

    private void setupViewPager() {
        if (viewPagerAdapter != null) {
            return;
        }
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setInterceptTabIndex(TAB_GIFT_INDEX);
//        viewPager.setInterceptDirection(TouchEventUtil.MOVE_DOWN);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                userProfileView.changeTabIcon(position);
                if (position == FragmentMe.TAB_GIFT_INDEX) {
                    appBarLayout.setExpanded(true, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void callChangeEventComment(Intent data) {

        if (data == null) {
            return;
        }

        ArrayList<ItemClassComments> arrComment = data.getExtras().getParcelableArrayList(ConstantBundleKey.BUNDLE_LIST_COMMENT);
        int positionOnListview = data.getExtras().getInt(ConstantBundleKey.BUNDLE_COMMENT_POSITION);

        if (arrComment == null)
            return;

        ListenerEventModel listenerEventModel = new ListenerEventModel();
        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.NEW_COMMENT);
        listenerEventModel.setTypeFragment(ListenerEventModel.TypeFragment.PROFILE_ME);
        NewCommentEventModel newCommentEventModel = new NewCommentEventModel();
        newCommentEventModel.setArrComment(arrComment);
//        newCommentEventModel.setPostId(arrayListProfileList.get(positionOnListview).getPost().getPostId());
        listenerEventModel.setNewCommentEventModel(newCommentEventModel);

        ((BaseActivity) getActivity()).eventChange(listenerEventModel);
    }

    private void callChangeEventProfile(Intent data) {

        if (data == null) {
            return;
        }

        isChangeProfileImage = data.getBooleanExtra(ConstantBundleKey.BUNDLE_CHANGE_PROFILE_IMAGE, false);
        boolean isChangeDisplayName = data.getBooleanExtra(ConstantBundleKey.BUNDLE_CHANGE_PROFILE_DISPLAY_NAME, false);

        if (!isChangeProfileImage && !isChangeDisplayName)
            return;

        ListenerEventModel listenerEventModel = new ListenerEventModel();
        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.EDIT_PROFILE);
        ((BaseActivity) getActivity()).eventChange(listenerEventModel);
    }


    public void stopVideosPlayInFeed() {
//        if (listAdapter != null) {
//            listAdapter.stopVideosItemPlayer();
//        }
        mVideoPlayerManager.stopAnyPlayback();
    }

    private void clipToolbarOffset() {
        if (toolbarOffset > toolbarHeight) {
            toolbarOffset = toolbarHeight;
        } else if (toolbarOffset < 0) {
            toolbarOffset = 0;
        }
    }

    private void scrollHeaderBar(int distance) {
        if (userProfileView != null) {
            userProfileView.setTranslationY(-distance);
        }

    }

    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.DELETE_POST) {
//            removeListAfterDeleteFromGrid(listenerEventModel.getDeletePostEventModel().getPostId());
            if (gridFragment != null) {
                gridFragment.removePostOnGird(listenerEventModel.getDeletePostEventModel().getPostId());
            }
            removePostOnGird(listenerEventModel.getDeletePostEventModel().getPostId());
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EDIT_PROFILE) {
            refreshData();
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_COMMENT) {
//            getDataFromCommentClass(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_LIKE) {
            if (listFragment != null) {
                listFragment.updateLike(listenerEventModel);
            }
        }

    }


    private void removePostOnGird(String postId) {

//        if (arrayListProfileGrid == null) {
//            return;
//        }
//
//        for (int i = 0; i < arrayListProfileGrid.size(); i++) {
//            if (arrayListProfileGrid.get(i).getPost().getPostId().equals(postId)) {
//                arrayListProfileGrid.remove(i);
//                gridAdapter.setItemsAndNotify(arrayListProfileGrid);
//                break;
//            }
//        }
    }


    public void handleNotificationMessage() {

        if (!mAppPreferences.isUserLogin()) {
            return;
        }

        if (isAppOwner) {

//            List<String> arrListUserIdSendMessage = AppsterChatManger.getInstance(getContext()).getListUserIdSendNewMessages();
            final int unreadMessage = AppsterApplication.mAppPreferences.getNumberUnreadMessage();
            userProfileView.handleNotificationMessage(unreadMessage != 0, unreadMessage);

        }
    }

    public void onScrollUpListView() {
        if (isFragmentUIActive()) {
            if (TAB_LIST_INDEX == viewPager.getCurrentItem() && listFragment != null) {
                if (listFragment.getAbleHandleRecyclerViewScrolling()) {
                    listFragment.onScrollUpListView();
                    appBarLayout.setExpanded(true, true);
                }
            } else if (TAB_GRID_INDEX == viewPager.getCurrentItem() && gridFragment != null) {
                if (gridFragment.getAbleHandleRecyclerViewScrolling()) {
                    gridFragment.onScrollUpListView();
                    appBarLayout.setExpanded(true, true);
                }
            }
        }
    }

    @Override
    public void LoadingDone() {
        if (mIsShouldShowList.get() && viewPager != null) {
            viewPager.setCurrentItem(TAB_LIST_INDEX);
            mIsShouldShowList.set(false);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter implements GiftRecyclerViewAdapter.CompleteSendGift {
        private static final int MAX_PAGE_COUNT = 3;
        private static final int MAX_PAGE_COUNT_APP_OWNER = 2;

        ViewPagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 1:
                    if (listFragment == null) {
                        listFragment = ListFragment.getInstance(userID, mUserName);
                    }
                    listFragment.setUserProfileView(userProfileView);
                    listFragment.setUserProfileDetails(userProfileDetails);
                    listFragment.setBlockCallback(FragmentMe.this);
                    listFragment.setLoadingDataListener(FragmentMe.this);
                    return listFragment;

                case 2:
                    if (giftFragment == null) {
                        giftFragment = new GiftFragment();
                        giftFragment.setCompleteSendGift(this);
                        giftFragment.setBackgroundTransparent(false);
                    }
                    giftFragment.setUserProfileDetails(userProfileDetails);
                    return giftFragment;


                case 0:
                default:
                    if (gridFragment == null) {
                        gridFragment = GridFragment.getInstance(userID, mUserName);
                    }
                    gridFragment.setUserProfileView(userProfileView);
                    gridFragment.setUserProfileDetails(userProfileDetails);

                    return gridFragment;
            }
        }

        @Override
        public int getCount() {
            return (isAppOwner) ? MAX_PAGE_COUNT_APP_OWNER : MAX_PAGE_COUNT;
        }


        @Override
        public void onSendSuccess(GiftItemModel ItemSend, long senderTotalBean, long senderTotalGold, long receiverTotalBean, long receiverTotalGoldFans, int votingScores, List<String> topFanList, List<DailyTopFanModel> dailyTopFans) {
            refreshHeader(false);
        }
    }

}
