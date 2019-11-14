package com.appster.profile;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.adapters.AdapterWallFeed;
import com.appster.comments.ItemClassComments;
import com.appster.features.edit_video.RecordActivity;
import com.appster.features.edit_video.ShortVideoConfig;
import com.appster.fragment.BaseVisibleItemFragment;
import com.appster.manager.VideosManager;
import com.appster.manager.WallFeedManager;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewCommentEventModel;
import com.appster.models.StreamModel;
import com.appster.models.UserModel;
import com.appster.models.UserPostModel;
import com.appster.models.event_bus_models.DeletePost;
import com.appster.models.event_bus_models.DeleteStream;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.viewholder.WallFeedItemViewHolder;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GetListPostByUserRequestModel;
import com.appster.webservice.request_models.StreamDefaultImageRequest;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CustomDialogUtils;
import com.apster.common.DialogManager;
import com.apster.common.LogUtils;
import com.apster.common.view.CustomScrollListener;
import com.google.android.material.appbar.AppBarLayout;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.stickyheaders.PagedLoadScrollListener;
import com.stickyheaders.StickyHeaderLayoutManager;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;
import static com.apster.common.FileUtility.getOutputMediaFile;

/**
 * Created by linh on 14/12/2016.
 */

public class ListFragment extends BaseVisibleItemFragment implements
        AppBarLayout.OnOffsetChangedListener,
        AdapterWallFeed.StreamCoverListener {
    private static final int TYPE_LIST = 0;

    public static final String BUNDLE_USER_ID = "user_id";
    public static final String BUNDLE_USER_NAME = "BUNDLE_USER_NAME";

    @Bind(R.id.rv_posts)
    RecyclerView recyclerView;
    @Bind(R.id.no_data)
    TextView noDataView;

    private AdapterWallFeed listAdapter;
    private StickyHeaderLayoutManager linearLayoutManager;
    private AdapterWallFeed.BlockCallback blockCallback;

    private UserProfileView userProfileView;
    private UserModel userProfileDetails;

    private ArrayList<UserPostModel> arrayListProfileList = new ArrayList<>();

    int nextIndexList = 0;
    boolean isLoading;
    private boolean isRefresh;

    private int userID;
    private String mUserName;
    private boolean isOwner;
    //    String displayName;
    private boolean isEndList;

    private boolean isNewPost = false;
    private boolean isChangeProfileImage = false;
    private String currentVideosLink = "";
    private PagedLoadScrollListener.LoadCompleteNotifier loadCompleteNotifier;
    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
//    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(metaData -> {
//
//    });
    VideosManager mVideoPlayerManager = VideosManager.getInstance();
    protected Uri fileUri;
    protected String mSlugRequestToChange;
    protected UserModel mCurrentUser;
    private AtomicBoolean mIsAbleHandleRecyclerViewScrolling = new AtomicBoolean(false);


    private LoadingDataListener mLoadingDataListener;

    public static ListFragment getInstance(int userID, String userName) {
        ListFragment f = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_USER_ID, userID);
        args.putString(BUNDLE_USER_NAME, userName);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        if (mRootView != null) {
            return mRootView;
        }
        mRootView = inflater.inflate(R.layout.fragment_me_list_grid_post, container, false);
        ButterKnife.bind(this, mRootView);
        mCurrentUser = AppsterApplication.mAppPreferences.getUserModel();
        nextIndexList = 0;
        String ownerId = "";
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            ownerId = mCurrentUser.getUserId();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getInt(BUNDLE_USER_ID);
            mUserName = bundle.getString(BUNDLE_USER_NAME);
        }
        isOwner = ownerId.equals(String.valueOf(userID));
        if (!isOwner) {
            noDataView.setText(getString(R.string.nothing_here));
        }
        setRecyclerView();

        getPostByUser(false);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNewPost) {
            refreshData();
        } else {
//                playVideosAfterLoadData();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isFragmentUIActive()) {
            playVideosAfterLoadData();
            Timber.e("ListFragment playVideosAfterLoadData");
        } else {
            handleStopVideosResetPlayer();
        }

        if (isVisibleToUser && isFragmentUIActive() && AppsterApplication.mAppPreferences.getIsRefreshGridAndList()) {
            refreshData();
            AppsterApplication.mAppPreferences.setIsRefreshGridAndList(false);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
    }

    @Override
    public void onPause() {
        super.onPause();
        handleStopVideosResetPlayer();
    }

    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.DELETE_POST) {
            removeListAfterDeleteFromGrid(listenerEventModel.getDeletePostEventModel().getPostId());
            removePostOnGird(listenerEventModel.getDeletePostEventModel().getPostId());
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EDIT_PROFILE) {
            refreshData();
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_COMMENT) {
            getDataFromCommentClass(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_LIKE) {
            updateLike(listenerEventModel);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        ButterKnife.unbind(this);
    }


    //region implemented methods ===================================================================
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventBus(DeletePost data) {
        Timber.d("on post deleted %s", data.mId);
        mCompositeSubscription.add(Observable.fromCallable(() -> getItemPositionById(data.mId, false))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDeleteItem, Timber::e));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventBus(DeleteStream data) {
        Timber.d("on stream deleted %s", data.mSlug);
        mCompositeSubscription.add(Observable.fromCallable(() -> getItemPositionById(data.mSlug, true))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDeleteItem, Timber::e));
    }

    private void onDeleteItem(int position) {
        Timber.d("item deleted at %d", position);
        if (position >= 0) {
            arrayListProfileList.remove(position);
            listAdapter.notifyItemRemoved(position);
        }
        checkIfNoData();
    }

    /**
     * @return the position of item in {@link #arrayListProfileList} which has the passed id
     * if there no item has the id then -1 will be returned;
     */
    private int getItemPositionById(String id, boolean isStream) {
        if (arrayListProfileList == null || arrayListProfileList.isEmpty() || TextUtils.isEmpty(id)) {
            return -1;
        }
        for (int i = 0; i < arrayListProfileList.size(); i++) {
            if (isStream) {
                StreamModel item = arrayListProfileList.get(i).getStream();
                if (item != null && id.equals(item.getSlug())) {
                    return i;
                }
            } else {
                ItemModelClassNewsFeed item = arrayListProfileList.get(i).getPost();
                if (item != null && id.equals(item.getPostId())) {
                    return i;
                }
            }
        }
        return -1;
    }
    //endregion ====================================================================================

    public void setBlockCallback(AdapterWallFeed.BlockCallback blockCallback) {
        this.blockCallback = blockCallback;
    }

    private void loadMore() {
        isLoading = true;
        Timber.e("** load more + " + arrayListProfileList.size());
        getPostByUser(false);
    }

    public void refreshData() {
        isLoading = true;
        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            if (loadCompleteNotifier != null) {

                loadCompleteNotifier.notifyRefresh();
            }
            nextIndexList = 0;
            isEndList = false;
            isRefresh = true;
            mVideoPlayerManager.stopAnyPlayback();
            currentVideosLink = "";

            if (arrayListProfileList != null) {
                arrayListProfileList.clear();
                listAdapter.notifyAllSectionsDataSetChanged();
            }
            Timber.e("refreshData==============");
            getPostByUser(false);
        } else {
            isLoading = false;
        }
    }

    public void setLoadingDataListener(LoadingDataListener mLoadingDataListener) {
        this.mLoadingDataListener = mLoadingDataListener;
    }


    private void setRecyclerView() {
//        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager = new StickyHeaderLayoutManager();
        if (listAdapter == null) {
            listAdapter = new AdapterWallFeed(arrayListProfileList, getActivity(), mVideoPlayerManager, userProfileDetails, type -> {
                userProfileView.updateFollowerCount(type);
                userProfileView.updateFollowButton(type);
            });
            listAdapter.setBlockCallback(blockCallback);
            listAdapter.setStreamCoverListener(this);
//            listAdapter.setOnItemDeleted(this);
//
//            listAdapter.setNewFeedChangeItemListener(new NewFeedChangeItemListener() {
//                @Override
//                public void onChangeFollow(int type) {
//
//                    userProfileView.updateFollowerCount(type);
//                    userProfileView.updateFollowButton(type);
//                }
//            });
        }
//      listAdapter.setRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        // For handle auto play videos
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                handlePlayVideosItem(newState);
            }

        });
        recyclerView.addOnScrollListener(new PagedLoadScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, LoadCompleteNotifier loadComplete) {
                loadCompleteNotifier = loadComplete;
                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    if (!isEndList) {
                        loadMore();
                    }

                } else {
                    ((BaseActivity) getActivity()).utility.showMessage("", getActivity().getString(R.string.no_internet_connection), getActivity());
                }
            }
        });
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        listAdapter.notifyAllSectionsDataSetChanged();
        recyclerView.addOnScrollListener(new CustomScrollListener(new CustomScrollListener.ScrollListener() {
            @Override
            public void onScrolling() {

            }

            @Override
            public void onSettling() {

            }

            @Override
            public void onNotScrolling() {
                mIsAbleHandleRecyclerViewScrolling.set(true);
            }

            @Override
            public void onScrolledDownward() {
                mIsAbleHandleRecyclerViewScrolling.set(false);
            }

            @Override
            public void onScrolledUp() {
                mIsAbleHandleRecyclerViewScrolling.set(true);
            }
        }));
    }

    public boolean getAbleHandleRecyclerViewScrolling() {
        return mIsAbleHandleRecyclerViewScrolling.get();
    }


    //=========== inner methods ====================================================================
    public void setUserProfileView(UserProfileView userProfileView) {
        if (this.userProfileView == null) {
            this.userProfileView = userProfileView;
        }
    }

    public void setUserProfileDetails(UserModel userProfileDetails) {
        if (userProfileDetails != null) {
            this.userProfileDetails = userProfileDetails;
            if (listAdapter != null) {
                listAdapter.setUserProfileDetails(userProfileDetails);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setNewPost(boolean newPost) {
        isNewPost = newPost;
    }

    public void playVideosAfterLoadData() {
        if (arrayListProfileList != null && arrayListProfileList.size() > 0) {
            if (recyclerView != null)
                recyclerView.post(() -> handlePlayVideosItem(RecyclerView.SCROLL_STATE_IDLE));
        }
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
        newCommentEventModel.setPostId(arrayListProfileList.get(positionOnListview).getPost().getPostId());
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

    void onGridTabUpdated(Intent data) {
        if (data != null) {

            boolean isDelete = data.getBooleanExtra(ConstantBundleKey.BUNDLE_DELETE_POST_ABLE, false);

            if (isDelete) {
                int position = data.getIntExtra(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, 0);
                if (arrayListProfileList.isEmpty() || position >= arrayListProfileList.size())
                    return;
                if (arrayListProfileList.get(position).getPost() != null) {
                    String idPost = arrayListProfileList.get(position).getPost().getPostId();
                    removeListAfterDeleteFromGrid(idPost);
                }
            } else {
                refreshData();
            }
        }
    }

    private void removeListAfterDeleteFromGrid(String idPost) {
        if (arrayListProfileList == null)
            return;

        if (arrayListProfileList.size() == 0)
            return;

        for (int i = 0; i < arrayListProfileList.size(); i++) {
            if (arrayListProfileList.get(i).getType() == Constants.LIST_USER_POST_NOMAL &&
                    arrayListProfileList.get(i).getPost().getPostId().equals(idPost)) {
                arrayListProfileList.remove(i);
                listAdapter.notifySectionRemoved(i);
                break;
            }
        }
    }

    public void updateEditPost(Intent data) {
        if (data != null) {
            int position = data.getIntExtra(ConstantBundleKey.BUNDLE_POSITION_EDIT_POST, 0);
            String title = data.getStringExtra(ConstantBundleKey.BUNDLE_TITLE_EDIT_POST);
            String address = data.getStringExtra(ConstantBundleKey.BUNDLE_ADDRESS_EDIT_POST);

            if (arrayListProfileList.size() > position) {

                arrayListProfileList.get(position).getPost().setTitle(title);
                arrayListProfileList.get(position).getPost().setAddress(address);

                listAdapter.notifyAllSectionsDataSetChanged();
            }
        } else {
            refreshData();
        }
    }

    public void getDataFromCommentClass(Intent data) {

        Bundle extras = data.getExtras();
        if (extras != null) {
            ArrayList<ItemClassComments> arrComment = extras.getParcelableArrayList(ConstantBundleKey.BUNDLE_LIST_COMMENT);
            if (arrComment == null) {
                arrComment = new ArrayList<>();
            }
            int position = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_POSITION);
            int commentCounts = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_COUNT);
            int postID = extras.getInt(ConstantBundleKey.BUNDLE_POST_ID_KEY);
            int commentType = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_TYPE);

            if (arrayListProfileList != null && arrayListProfileList.size() > position) {
                UserPostModel postModel = arrayListProfileList.get(position);
                if (commentType == Constants.COMMENT_TYPE_STREAM) {
                    if (isSameRecordStream(postModel, postID)) {
                        StreamModel streamModel = postModel.getStream();
                        streamModel.getCommentList().clear();
                        streamModel.getCommentList().addAll(arrComment);
                        streamModel.setTotalCommentCount(commentCounts);
                        listAdapter.notifySectionItemChanged(position, 0);
                    }
                } else {
                    if (isSamePost(postModel, String.valueOf(postID))) {
                        ItemModelClassNewsFeed post = postModel.getPost();
                        postModel.getPost().getCommentList().clear();
                        postModel.getPost().getCommentList().addAll(arrComment);
                        post.setCommentCount(commentCounts);
                        listAdapter.notifySectionItemChanged(position, 0);
                    }
                }
            }
        }
    }

    private boolean isSamePost(UserPostModel userPostModel, String postID) {
        return !userPostModel.isStreamItem() && userPostModel.getPost() != null && userPostModel.getPost().getPostId().equalsIgnoreCase(postID);
    }

    private boolean isSameRecordStream(UserPostModel userPostModel, int recordId) {
        return userPostModel.isStreamItem() && userPostModel.getStream().getStreamId() == recordId;
    }

    void updateLike(ListenerEventModel listenerEventModel) {

        if (arrayListProfileList == null) {
            return;
        }

        for (int i = 0; i < arrayListProfileList.size(); i++) {
            ItemModelClassNewsFeed item = arrayListProfileList.get(i).getPost();
            if (item != null && item.getPostId().equals(listenerEventModel.getNewLikeEventModel().getPostId())) {
                item.setIsLike(listenerEventModel.getNewLikeEventModel().getIsLike());
                item.setLikeCount(listenerEventModel.getNewLikeEventModel().getLikeCount());
                listAdapter.notifyAllSectionsDataSetChanged();
                break;
            }
        }

    }

    private void removePostOnGird(String postId) {

    }

    private void getDataFromCommentClass(ListenerEventModel listenerEventModel) {
        if (arrayListProfileList == null) {
            return;
        }

        for (int i = 0; i < arrayListProfileList.size(); i++) {
            if (arrayListProfileList.get(i).getType() == Constants.LIST_USER_POST_NOMAL &&
                    arrayListProfileList.get(i).getPost().getPostId().equals(listenerEventModel.getNewCommentEventModel().getPostId())) {
                arrayListProfileList.get(i).getPost().getCommentList().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
                break;
            }
        }

        listAdapter.notifyAllSectionsDataSetChanged();
    }

    private void getPostByUser(final boolean isShowingDialog) {

        if (!DialogManager.isShowing() && isShowingDialog) {
            DialogManager.getInstance().showDialog(getActivity(), getString(R.string.connecting_msg));
        }

        GetListPostByUserRequestModel requestModel = new GetListPostByUserRequestModel();
        requestModel.setProfileId(userID);
        requestModel.setUserName(mUserName);
        requestModel.setViewType(TYPE_LIST);
        requestModel.setLimit(Constants.PAGE_LIMITED);
        requestModel.setNextId(nextIndexList);

        mCompositeSubscription.add(AppsterWebServices.get().getListPostByUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), requestModel)
                .subscribe(getListPostByUserDataResponseModel -> {
                    if (DialogManager.isShowing()) {
                        DialogManager.getInstance().dismisDialog();
                    }

                    if (!isFragmentUIActive()) {
                        return;
                    }

                    isLoading = false;
                    updateUI(getListPostByUserDataResponseModel, isShowingDialog);
                    if (mLoadingDataListener != null) mLoadingDataListener.LoadingDone();
                }, error -> {
                    if (DialogManager.isShowing()) {
                        DialogManager.getInstance().dismisDialog();
                    }
                    isLoading = false;
                    Timber.e(error.getMessage());
                    onErrorWebServiceCall(error.getMessage(), Constants.RETROFIT_ERROR);
                    listAdapter.notifyDataSetChanged();
                    if (mLoadingDataListener != null) mLoadingDataListener.LoadingDone();
                }));
    }

    private void updateDataInBackGround(BaseResponse<BaseDataPagingResponseModel<UserPostModel>> data, int viewType) {
        if (data.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            return;
        }
        if (data.getData().getResult() != null) {
            arrayListProfileList.addAll(data.getData().getResult());
        }
        isEndList = data.getData().isEnd();
        nextIndexList = data.getData().getNextId();
    }

    private void updateUI(BaseResponse<BaseDataPagingResponseModel<UserPostModel>> userProfileResponseModel, boolean isShowingDialog) {
        if (userProfileResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            return;
        }

        if (isRefresh) {
            arrayListProfileList.clear();
            isRefresh = false;
        }

        if (userProfileResponseModel.getData().getResult() != null) {
            arrayListProfileList.addAll(userProfileResponseModel.getData().getResult());
            checkUpdateUserImageOnPost();
            listAdapter.notifyAllSectionsDataSetChanged();
        }

        isEndList = userProfileResponseModel.getData().isEnd();
        nextIndexList = userProfileResponseModel.getData().getNextId();
        if (isEndList) {
            if (loadCompleteNotifier != null) {
                loadCompleteNotifier.notifyLoadExhausted();
            }
        } else {
            if (loadCompleteNotifier != null) {
                loadCompleteNotifier.notifyLoadComplete();
                loadCompleteNotifier = null;
            }
        }

        if (isNewPost) {
            isNewPost = false;
            linearLayoutManager.scrollToPosition(0);
        }
        checkIfNoData();
    }

    private void checkUpdateUserImageOnPost() {
        if (AppsterApplication.mAppPreferences.isUserLogin() && mCurrentUser != null && userProfileDetails != null
                && mCurrentUser.getUserId().equals(userProfileDetails.getUserId())) {
            mCurrentUser = AppsterApplication.mAppPreferences.getUserModel();
            userProfileDetails.setUserImage(mCurrentUser.getUserImage());
            listAdapter.setUserProfileDetails(mCurrentUser);
        }
    }

    private void checkIfNoData() {
        if (arrayListProfileList == null || arrayListProfileList.size() <= 0) {
            if (noDataView != null) noDataView.setVisibility(View.VISIBLE);
        } else {
            if (noDataView != null) noDataView.setVisibility(View.GONE);
        }
    }

    private void handlePlayVideosItem(int newState) {
        WallFeedItemViewHolder itemViewHolder = (WallFeedItemViewHolder) linearLayoutManager.getFirstVisibleItemViewHolder(true);
        if (itemViewHolder != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
            UserPostModel model = itemViewHolder.getPostData();
            if (model != null) {
                String url = "";
                if (model.isStreamItem()) {
                    if (model.getStream().isStreamBeingLive()) {
                        url = model.getStream().getStreamUrl();
                    } else {
                        url = model.getStream().getStreamRecording().getDownloadUrl();
                    }
                } else {
                    ItemModelClassNewsFeed itemFeed = model.getPost();
                    if (itemFeed.getMediaType() == CommonDefine.TYPE_VIDEO) {
                        url = model.getPost().getMediaVideo();
                    }

                }
                if (!StringUtil.isNullOrEmptyString(url) && getVisibilityPercents(itemViewHolder.itemView) > Constants.PERCENT_MIN_VISIBLE) {
                    if (!url.equalsIgnoreCase(currentVideosLink)) {
                        currentVideosLink = url;
                        Timber.e("play video %s", currentVideosLink);
                        itemViewHolder.autoPlayVideo(url);
                    }

                } else {
                    handleStopVideosResetPlayer();
                }

            } else {
                handleStopVideosResetPlayer();
            }

        } else {
            if (itemViewHolder != null && getVisibilityPercents(itemViewHolder.itemView) <= Constants.PERCENT_MAX_GONE) {
                LogUtils.logV("NCS", "Stop Videos");
                itemViewHolder.stopThreadIncreateViewCount();
                handleStopVideosResetPlayer();
            }
        }
    }


    public void handleStopVideosResetPlayer() {
        LogUtils.logV("NCS", "handleStopVideosResetPlayer");
        WallFeedManager.getInstance().stopTimerTask();
        currentVideosLink = "";
        mVideoPlayerManager.stopAnyPlayback();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventBusRefreshFragment event) {
//        Timber.e("refreshData---onEventMainThread=====================");
//        refreshData();
//
//    }

    public void refreshRecyclerView() {
        if (listAdapter != null) {
            listAdapter.notifyAllSectionsDataSetChanged();
            playVideosAfterLoadData();
        }
    }
    //region change stream cover

    @Override
    public void onChangeStreamCoverRequest(String slug) {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        mCompositeSubscription.add(rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        mSlugRequestToChange = slug;
                        CustomDialogUtils.openRecordVideoDialog(getContext(),
                                getString(R.string.select_a_stream_cover_from), v -> takePictureFromCamera(), v -> takePictureFromGallery());
                    }
                }));
    }

    void takePictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        photoPickerIntent.setType("image/*");
        if (getContext() != null && photoPickerIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, Constants.REQUEST_COVER_FROM_LIBRARY);
        }
    }

    void takePictureFromCamera() {
        ShortVideoConfig config = RecordActivity.createConfig();
        RecordActivity.startActivity(this, Constants.REQUEST_PIC_FROM_CAMERA,
                config);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri imageCroppedURI;
            try {
                imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
            } catch (NullPointerException e) {
                Timber.d(e);
                return;
            }
            switch (requestCode) {
                case Constants.REQUEST_PIC_FROM_CAMERA:
                    Timber.e("REQUEST_PIC_FROM_CAMERA");
                    fileUri = data.getData();
                    if (fileUri == null) {
                        return;
                    }
                    uploadNewCoverImage(fileUri, mSlugRequestToChange);
                    break;
                case Constants.REQUEST_COVER_FROM_LIBRARY:
                    Timber.e("REQUEST_COVER_FROM_LIBRARY");
                    fileUri = data.getData();
                    if (fileUri == null) {
                        return;
                    }
                    performCrop(fileUri, imageCroppedURI);
                    break;

                case Constants.REQUEST_PIC_FROM_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        uploadNewCoverImage(resultUri, mSlugRequestToChange);
                    } else {
                        Toast.makeText(getContext(), R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private void uploadNewCoverImage(Uri resultUri, String streamSlug) {
        // TODO: 5/15/17 implement upload cover image
//        Timber.e("upload %s - %s",resultUri.toString(),streamSlug);
        StreamDefaultImageRequest request = new StreamDefaultImageRequest(streamSlug, new File(resultUri.getPath()));

        mCompositeSubscription.add(AppsterWebServices.get().saveFirstStreamImage("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request.build())
                .filter(booleanBaseResponse -> isFragmentUIActive())
                .subscribe(streamPostImageResponse -> {
                    if (streamPostImageResponse.getData()) {
                        Toast.makeText(getContext().getApplicationContext(), getString(R.string.cover_image_changed), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Timber.e(error.getMessage())));
    }

    private void performCrop(Uri inPut, Uri outPut) {
        // take care of exceptions
        try {
            int maxWidth = 800;// width and height;
            int maxHeight = 800;// width and height;
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
            options.setHideBottomControls(true);
            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.color_header_title));

            UCrop uCrop = UCrop.of(inPut, outPut);
            uCrop = uCrop.withAspectRatio(1, 1);//set squared output
            uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
            uCrop.withOptions(options);
            uCrop.start(getContext(), this, Constants.REQUEST_PIC_FROM_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
            anfe.printStackTrace();
        }
    }

    public Uri getOutputMediaFileUri(int type) throws NullPointerException {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //endregion

    public void onScrollUpListView() {
        scrollTopUpRecyclerView(recyclerView, false);
    }
}
