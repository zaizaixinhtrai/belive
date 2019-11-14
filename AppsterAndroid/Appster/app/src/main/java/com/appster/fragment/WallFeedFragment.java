package com.appster.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.adapters.AdapterWallFeed;
import com.appster.comments.ItemClassComments;
import com.appster.features.edit_video.RecordActivity;
import com.appster.features.edit_video.ShortVideoConfig;
import com.appster.main.MainActivity;
import com.appster.manager.VideosManager;
import com.appster.manager.WallFeedManager;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewCommentEventModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.ReportEvent;
import com.appster.models.StreamModel;
import com.appster.models.UserPostModel;
import com.appster.models.event_bus_models.DeletePost;
import com.appster.models.event_bus_models.DeleteStream;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.models.event_bus_models.EventBusRefreshWallfeedTab;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.viewholder.WallFeedItemViewHolder;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GetWallFeedRequest;
import com.appster.webservice.request_models.StreamDefaultImageRequest;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CustomDialogUtils;
import com.apster.common.DialogManager;
import com.apster.common.LogUtils;
import com.apster.common.UiUtils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.stickyheaders.PagedLoadScrollListener;
import com.stickyheaders.SectioningAdapter;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;
import static com.apster.common.FileUtility.getOutputMediaFile;

/**
 * Created by sonnguyen on 11/15/16.
 */

public class WallFeedFragment extends BaseVisibleItemFragment implements
        AdapterWallFeed.BlockCallback,
        AdapterWallFeed.StreamCoverListener {
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.noDataView)
    LinearLayout noDataView;
    @Bind(R.id.imgSearchUser)
    ImageButton imgSearchUser;


    private View rootView;
    AdapterWallFeed mAdapter;
    boolean isPageEnd;
    int nextPageIndex = 0;
    List<UserPostModel> arrayWallFeed;
    PagedLoadScrollListener.LoadCompleteNotifier loadCompleteNotifier;
    StickyHeaderLayoutManager layoutManager;
    String currentVideosLink = "";

    /**
     * //     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     * //
     */

    VideosManager mVideoPlayerManager = VideosManager.getInstance();
    protected Uri fileUri;
    protected String mSlugRequestToChange;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragement_wall_feed, container, false);
        ButterKnife.bind(this, rootView);
        layoutManager = new StickyHeaderLayoutManager();
        initRecycleView();

        imgSearchUser.setOnClickListener(v -> ((MainActivity) getActivity()).changeTabWhenClickSearch());
        return rootView;


    }

    public static WallFeedFragment getInstance() {
        return new WallFeedFragment();
    }

    private void initRecycleView() {
        arrayWallFeed = WallFeedManager.getInstance().getArrayWallFeed();
        mAdapter = new AdapterWallFeed(arrayWallFeed, getActivity(), mVideoPlayerManager);
        mAdapter.setBlockCallback(this);
        mAdapter.setStreamCoverListener(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);


        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        UiUtils.setColorSwipeRefreshLayout(swipeRefreshLayout);

        // For handle auto play videos
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                handlePlayVideosItem(newState);
            }

        });

        mRecyclerView.addOnScrollListener(new PagedLoadScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, LoadCompleteNotifier loadComplete) {
                loadCompleteNotifier = loadComplete;
                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    loadData(false);
                } else {
                    ((BaseActivity) getActivity()).utility.showMessage("", getActivity().getString(R.string.no_internet_connection), getActivity());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void reloadDataServer() {
        nextPageIndex = 0;
        loadData(false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshRecyclerView();
        LogUtils.logV("NCS", "playVideosAfterLoadData  onResume");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        handleStopVideosResetPlayer();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded) {
            if (CheckNetwork.isNetworkAvailable(getActivity())) {
                nextPageIndex = 0;
                loadData(true);
            } else {
                ((BaseActivity) getActivity()).utility.showMessage("", getActivity().getString(R.string.no_internet_connection), getActivity());
            }
            _areLecturesLoaded = true;
        }
        if (getUserVisibleHint()) {
            ((BaseToolBarActivity) getActivity()).setTopBarTile(getString(R.string.wall_feed));
            ((BaseToolBarActivity) getActivity()).handleToolbar(true);
//            ((BaseToolBarActivity) getActivity()).defaultMenuIcon();
            ((BaseToolBarActivity) getActivity()).handleNewPushNotification(0);
            refreshRecyclerView();
            LogUtils.logV("NCS", "playVideosAfterLoadData  setUserVisibleHint");
        } else {
            handleStopVideosResetPlayer();
        }

        if (isVisibleToUser && arrayWallFeed != null && arrayWallFeed.size() == 0) {
            noDataView.setVisibility(View.GONE);
            refreshData();
        }

        if (isVisibleToUser && AppsterApplication.mAppPreferences.getIsNewPostFromFollowingUsers() && mAlreadyOnTopList.get()) {
            refreshData();
        }

        if (!isVisibleToUser) {
            mAlreadyOnTopList.set(detectIfTopOfRecyclerView());
        }
    }

    private AtomicBoolean mAlreadyOnTopList = new AtomicBoolean(false);

    @Override
    public void OnBlockSuccessfully() {
//        refreshData();
        EventBus.getDefault().post(new EventBusRefreshFragment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusRefreshFragment event) {
        reloadDataServer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusRefreshWallfeedTab eventBusdata) {
        refreshData();
    }


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
            arrayWallFeed.remove(position);
            mAdapter.notifySectionRemoved(position);
        }
    }

    /**
     * @return the position of item in {@link #arrayWallFeed} which has the passed id
     * if there no item has the id then -1 will be returned;
     */
    private int getItemPositionById(String id, boolean isStream) {
        if (arrayWallFeed == null || arrayWallFeed.isEmpty() || TextUtils.isEmpty(id)) {
            return -1;
        }
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            if (isStream) {
                StreamModel item = arrayWallFeed.get(i).getStream();
                if (item != null && id.equals(item.getSlug())) {
                    return i;
                }
            } else {
                ItemModelClassNewsFeed item = arrayWallFeed.get(i).getPost();
                if (item != null && id.equals(item.getPostId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void handleShowNotificationRed() {
        if (isFragmentUIActive()) {
            ((MainActivity) getContext()).handleShowWallfeedNotificationRed();
        }
    }

    private void refreshData() {
        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            nextPageIndex = 0;
            isPageEnd = false;
            loadData(false);
            AppsterApplication.mAppPreferences.setIsIsNewPostFromFollowingUsers(false);
            if (loadCompleteNotifier != null) {
                loadCompleteNotifier.notifyRefresh();
                loadCompleteNotifier = null;
            }
        } else {
            ((BaseActivity) getActivity()).utility.showMessage("", getActivity().getString(R.string.no_internet_connection), getActivity());
            swipeRefreshLayout.setRefreshing(false);
        }
        mVideoPlayerManager.stopAnyPlayback();
        currentVideosLink = "";
    }

    public boolean detectIfTopOfRecyclerView() {
        if (layoutManager != null && arrayWallFeed != null && !arrayWallFeed.isEmpty()) {
            SectioningAdapter.ItemViewHolder viewHolder = layoutManager.getFirstVisibleItemViewHolder(true);
            int idInAdapter;
            int idInList;
            if (viewHolder != null) {
                if (viewHolder instanceof WallFeedItemViewHolder) {
                    final WallFeedItemViewHolder item = (WallFeedItemViewHolder) viewHolder;
                    if (item.getPostData().getPost() != null) {
                        idInAdapter = Integer.parseInt(item.getPostData().getPost().getPostId());
                    } else {
                        idInAdapter = item.getPostData().getStream().getStreamId();
                    }

                    if (arrayWallFeed.get(0).getPost() != null) {
                        idInList = Integer.parseInt(arrayWallFeed.get(0).getPost().getPostId());
                    } else {
                        idInList = arrayWallFeed.get(0).getStream().getStreamId();
                    }
                    Timber.e("idInAdapter =" + idInAdapter);
                    Timber.e("idInList =" + idInList);
                    return idInAdapter == idInList;
                }
            }
        }
        return false;
    }

    private AtomicBoolean mIsLoadingData = new AtomicBoolean(false);

    public void loadData(boolean isShowingDialog) {
        if (mIsLoadingData.get()) {
            return;
        }
        mIsLoadingData.set(true);
        if (!DialogManager.isShowing() && isShowingDialog) {
            DialogManager.getInstance().showDialog(getActivity(), getString(R.string.connecting_msg));
        }
        GetWallFeedRequest request = new GetWallFeedRequest();
        request.setNextId(nextPageIndex);
        request.setLimit(Constants.ITEM_PAGE_LIMITED_FOR_GRID);
        mCompositeSubscription.add(AppsterWebServices.get().getWallFeed(AppsterApplication.mAppPreferences.getUserTokenRequest(), request)
                .subscribe(new Subscriber<BaseResponse<BaseDataPagingResponseModel<UserPostModel>>>() {
                    @Override
                    public void onCompleted() {
                        mIsLoadingData.set(false);
                        swipeRefreshLayout.setRefreshing(false);
                        if (DialogManager.isShowing()) {
                            DialogManager.getInstance().dismisDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsLoadingData.set(false);
                        if (DialogManager.isShowing()) {
                            DialogManager.getInstance().dismisDialog();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Timber.e(e);
                    }

                    @Override
                    public void onNext(BaseResponse<BaseDataPagingResponseModel<UserPostModel>> getListPostDataResponse) {
                        mIsLoadingData.set(false);
                        if (getListPostDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            if (nextPageIndex == 0) {
                                arrayWallFeed.clear();
                            }
                            isPageEnd = getListPostDataResponse.getData().isEnd();
                            if (isPageEnd) {
                                if (loadCompleteNotifier != null) {
                                    loadCompleteNotifier.notifyLoadExhausted();
//                                            loadCompleteNotifier = null;

                                }
                            } else {
                                if (loadCompleteNotifier != null) {
                                    loadCompleteNotifier.notifyLoadComplete();
                                    loadCompleteNotifier = null;
                                }
                            }

                            arrayWallFeed.addAll(getListPostDataResponse.getData().getResult());
                            WallFeedManager.getInstance().setArrayWallFeed(arrayWallFeed);
                            mAdapter.notifyAllSectionsDataSetChanged();
                            if (nextPageIndex == 0 && layoutManager != null) {
                                layoutManager.scrollToPosition(0);
                            }
                            nextPageIndex = getListPostDataResponse.getData().getNextId();
                            playVideosAfterLoadData();
                            handleShowNotificationRed();
                        }

                        if (!arrayWallFeed.isEmpty()) {
                            noDataView.setVisibility(View.GONE);

                        } else {
                            noDataView.setVisibility(View.VISIBLE);
                        }


                    }
                }));

        if (isShowingDialog) {
            AppsterApplication.mAppPreferences.setIsIsNewPostFromFollowingUsers(false);
        }
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
                case Constants.COMMENT_REQUEST:
                    getDataFromCommentClass(data);
                    break;
                case Constants.REQUEST_CODE_EDIT_PROFILE:
                    break;
                case Constants.REQUEST_EDIT_POST:
                    updatePostAfterEdit(data);
                    break;
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
                        Toast.makeText(getContext().getApplicationContext(), R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
        }
    }

    private void updatePostAfterEdit(Intent data) {
        if (data != null) {

            int position = data.getIntExtra(ConstantBundleKey.BUNDLE_POSITION_EDIT_POST, 0);
            if (arrayWallFeed == null || arrayWallFeed.size() <= position) {
                return;
            }
            String des = data.getStringExtra(ConstantBundleKey.BUNDLE_TITLE_EDIT_POST);
            String address = data.getStringExtra(ConstantBundleKey.BUNDLE_ADDRESS_EDIT_POST);
            String postID = data.getStringExtra(ConstantBundleKey.BUNDLE_ID_EDIT_POST);
            UserPostModel post = arrayWallFeed.get(position);
            if (post != null && !post.isStreamItem()) {
                if (post.getPost().getPostId().equalsIgnoreCase(postID)) {
                    arrayWallFeed.get(position).getPost().setTitle(des);
                    arrayWallFeed.get(position).getPost().setAddress(address);
                    mAdapter.notifyAllSectionsDataSetChanged();
                } else {
                    for (int i = 0; i < arrayWallFeed.size(); i++) {
                        UserPostModel userpost = arrayWallFeed.get(i);
                        if (isUpdateAblePost(postID, userpost)) {
                            arrayWallFeed.get(i).getPost().setTitle(des);
                            arrayWallFeed.get(i).getPost().setAddress(address);
                            mAdapter.notifyAllSectionsDataSetChanged();
                            break;
                        }
                    }
                }
            }

        }
    }

    private boolean isUpdateAblePost(String postID, UserPostModel userpost) {
        return userpost != null && !userpost.isStreamItem() && userpost.getPost().getPostId().equalsIgnoreCase(postID);
    }

    private void getDataFromCommentClass(Intent data) {
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
            int currentCommentCount;
            if (arrayWallFeed != null && arrayWallFeed.size() > position) {
                UserPostModel postModel = arrayWallFeed.get(position);
                if (commentType == Constants.COMMENT_TYPE_STREAM) {
                    if (isSameRecordStream(postModel, postID)) {
                        StreamModel streamModel = postModel.getStream();

                        streamModel.getCommentList().clear();
                        streamModel.getCommentList().addAll(arrComment);
                        streamModel.setTotalCommentCount(commentCounts);
                        mAdapter.notifySectionItemChanged(position, 0);
                    }
                } else {
                    if (isSamePost(postModel, String.valueOf(postID))) {
                        ItemModelClassNewsFeed post = postModel.getPost();
                        post.getCommentList().clear();
                        post.getCommentList().addAll(arrComment);
                        post.setCommentCount(commentCounts);
                        mAdapter.notifySectionItemChanged(position, 0);
                    }
                }
            }
        }
    }

    private boolean isSameSlug(UserPostModel userPostModel, String slug) {
        return userPostModel.isStreamItem() && userPostModel.getStream() != null && userPostModel.getStream().getSlug().equalsIgnoreCase(slug);
    }

    private boolean isSamePost(UserPostModel userPostModel, String postID) {
        return !userPostModel.isStreamItem() && userPostModel.getPost() != null && userPostModel.getPost().getPostId().equalsIgnoreCase(postID);
    }

    private boolean isSameRecordStream(UserPostModel userPostModel, int recordId) {
        return userPostModel.isStreamItem() && userPostModel.getStream().getStreamId() == recordId;
    }

    private void refreshRecyclerView() {
        if (mAdapter != null) {
            mAdapter.notifyAllSectionsDataSetChanged();
            playVideosAfterLoadData();
        }
    }

    private void playVideosAfterLoadData() {
        if (arrayWallFeed != null && !arrayWallFeed.isEmpty()) {
            mRecyclerView.post(() -> handlePlayVideosItem(RecyclerView.SCROLL_STATE_IDLE));
        }
    }

    private void handlePlayVideosItem(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            WallFeedItemViewHolder itemViewHolder = (WallFeedItemViewHolder) layoutManager.getFirstVisibleItemViewHolder(true);
            if (itemViewHolder != null) {
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
//                        itemViewHolder.autoPlayVideo(url);
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
    }

    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {
        super.eventChange(listenerEventModel);
        if (arrayWallFeed == null || arrayWallFeed.isEmpty())
            return;

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.DELETE_POST) {
            removePostIsDelete(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EDIT_PROFILE) {
            changeUserProfileImage();
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_COMMENT) {
            addCommentToPost(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_LIKE) {
            updateLike(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_EVENT_FROM_USER_POST_DETAIL) {
            updateFollowStatus(listenerEventModel.getFollowStatusChangedEvent());
            addMultiComment(listenerEventModel);
            addMultiLike(listenerEventModel);
            addMultiReport(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.FOLLOW_USER) {
            updateFollowStatus(listenerEventModel.getFollowStatusChangedEvent());
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EVENT_VIEW_VIDEOS) {
            viewVideosCount(listenerEventModel);
            return;
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EVENT_REPORT) {
            reportEvent(listenerEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_EVENT_FROM_LIVE_STREAM) {
            updateLike(listenerEventModel);
            if (listenerEventModel.getFollowStatusChangedEvent() != null) {
                updateFollowStatus(listenerEventModel.getFollowStatusChangedEvent());
            }
        }
        mAdapter.notifyAllSectionsDataSetChanged();

    }

    private void updateLike(ListenerEventModel listenerEventModel) {
        if (listenerEventModel.getNewLikeEventModel() == null) {
            return;
        }
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (listenerEventModel.getNewLikeEventModel().isStream()) {
                if (isSameSlug(postModel, listenerEventModel.getNewLikeEventModel().getSlug())) {
                    arrayWallFeed.get(i).getStream().setLike(listenerEventModel.getNewLikeEventModel().getIsLike());
                    arrayWallFeed.get(i).getStream().setLikeCount(listenerEventModel.getNewLikeEventModel().getLikeCount());
                    break;
                }
            } else {
                if (isSamePost(postModel, listenerEventModel.getNewLikeEventModel().getPostId())) {
                    arrayWallFeed.get(i).getPost().setIsLike(listenerEventModel.getNewLikeEventModel().getIsLike());
                    arrayWallFeed.get(i).getPost().setLikeCount(listenerEventModel.getNewLikeEventModel().getLikeCount());

                    break;
                }
            }


        }
    }

    public void updateFollowStatus(FollowStatusChangedEvent event) {
        if (event == null || arrayWallFeed == null || arrayWallFeed.isEmpty()) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (event.isStream()) {
                if (postModel.isStreamItem() && arrayWallFeed.get(i).getStream().getPublisher().getUserId().equals(event.getUserId())) {
                    arrayWallFeed.get(i).getStream().getPublisher().setIsFollow(event.getFollowType());
                }
            } else {
                if (!postModel.isStreamItem() && arrayWallFeed.get(i).getPost().getUserId().equals(event.getUserId())) {
                    arrayWallFeed.get(i).getPost().setIsFollow(event.getFollowType());
                }

            }

        }

    }

    private void addCommentToPost(ListenerEventModel listenerEventModel) {
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (!listenerEventModel.getNewCommentEventModel().isStream() && isSamePost(postModel, listenerEventModel.getNewCommentEventModel().getPostId())) {
                arrayWallFeed.get(i).getPost().getCommentList().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
                break;
            }
        }
    }

    private void changeUserProfileImage() {
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (postModel.isStreamItem()) {
                if (arrayWallFeed.get(i).getStream().getPublisher().getUserId().equalsIgnoreCase(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                    arrayWallFeed.get(i).getStream().getPublisher().setUserImage(AppsterApplication.mAppPreferences.getUserModel().getUserImage());
                    arrayWallFeed.get(i).getStream().getPublisher().setDisplayName(AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
                }
            } else {
                if (arrayWallFeed.get(i).getPost().getNfs_userid().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                    arrayWallFeed.get(i).getPost().setUserImage(AppsterApplication.mAppPreferences.getUserModel().getUserImage());
                    arrayWallFeed.get(i).getPost().setDisplayName(AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
                }
            }

        }
    }

    private void removePostIsDelete(ListenerEventModel listenerEventModel) {
        if (listenerEventModel.getDeletePostEventModel() == null) {
            return;
        }
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (listenerEventModel.getDeletePostEventModel().isStream()) {
                if (isSameSlug(postModel, listenerEventModel.getDeletePostEventModel().getSlug())) {
                    arrayWallFeed.remove(i);
                    break;
                }
            } else {
                if (isSamePost(postModel, listenerEventModel.getDeletePostEventModel().getPostId())) {
                    arrayWallFeed.remove(i);
                    break;
                }
            }

        }

    }

    private void viewVideosCount(ListenerEventModel listenerEventModel) {
        if (listenerEventModel.getViewVideosEvent() == null) {
            return;
        }
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
//            if(listenerEventModel.getViewVideosEvent().isStream()){
////                if (arrayWallFeed.get(i).getStream().getSlug().equals(listenerEventModel.getNewCommentEventModel().getSlug())) {
////                    arrayWallFeed.get(i).getStream().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
////                    break;
////                }
//            }else {
            if (!postModel.isStreamItem() && arrayWallFeed.get(i).getPost().getPostId().equals(listenerEventModel.getViewVideosEvent().getPostId())) {
                arrayWallFeed.get(i).getPost().setViewCount(listenerEventModel.getViewVideosEvent().getViewCount());
                break;
            }
        }

//        }
    }

    private void reportEvent(ListenerEventModel listenerEventModel) {
        if (listenerEventModel.getReportEvent() == null) {
            return;
        }
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if (listenerEventModel.getReportEvent().isStream()) {
                if (isSameSlug(postModel, listenerEventModel.getReportEvent().getSlug())) {
                    arrayWallFeed.get(i).getStream().setIsReport(listenerEventModel.getReportEvent().getIsReport());
                    break;
                }
            } else {
                if (isSamePost(postModel, listenerEventModel.getReportEvent().getPostId())) {
                    arrayWallFeed.get(i).getPost().setIsReport(listenerEventModel.getReportEvent().getIsReport());
                    break;
                }

            }

        }

    }


    private void addMultiComment(ListenerEventModel listenerEventModel) {

        ArrayList<NewCommentEventModel> arrNewCommentEvnt = listenerEventModel.getArrNewCommentEvnt();
        if (arrNewCommentEvnt != null) {

            NewCommentEventModel newCommentEventModel = new NewCommentEventModel();
            for (int i = 0; i < arrNewCommentEvnt.size(); i++) {

                newCommentEventModel.setPostId(arrNewCommentEvnt.get(i).getPostId());
                newCommentEventModel.setArrComment(arrNewCommentEvnt.get(i).getArrComment());
                listenerEventModel.setNewCommentEventModel(newCommentEventModel);
                addCommentToPost(listenerEventModel);
            }
        }
    }

    private void addMultiLike(ListenerEventModel listenerEventModel) {

        ArrayList<NewLikeEventModel> arrNewLikeEvent = listenerEventModel.getArrNewLikeEvnt();
        if (arrNewLikeEvent != null) {

            for (int i = 0; i < arrNewLikeEvent.size(); i++) {
                listenerEventModel.setNewLikeEventModel(arrNewLikeEvent.get(i));
                updateLike(listenerEventModel);
            }
        }
    }

    private void addMultiReport(ListenerEventModel listenerEventModel) {

        ArrayList<ReportEvent> arrReportEvent = listenerEventModel.getArrReportEvent();
        if (arrReportEvent != null) {

            for (int i = 0; i < arrReportEvent.size(); i++) {
                listenerEventModel.setReportEvent(arrReportEvent.get(i));
                reportEvent(listenerEventModel);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        LogUtils.logV("NCS", "onPause");
        handleStopVideosResetPlayer();
    }

    public void handleStopVideosResetPlayer() {
        LogUtils.logV("NCS", "handleStopVideosResetPlayer");
        WallFeedManager.getInstance().stopTimerTask();
        currentVideosLink = "";
        mVideoPlayerManager.stopAnyPlayback();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mVideoPlayerManager.resetMediaPlayer();
        ButterKnife.unbind(this);
    }

    public void handleScrollAndLoadData() {
        if (AppsterApplication.mAppPreferences.getIsNewPostFromFollowingUsers()) {
            refreshData();
        } else {
            onScrollUpListView();
        }
    }

    public void onScrollUpListView() {
        scrollTopUpRecyclerView(mRecyclerView, false);
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
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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

    private void uploadNewCoverImage(Uri resultUri, String streamSlug) {
        // TODO: 5/15/17 implement upload cover image
        StreamDefaultImageRequest request = new StreamDefaultImageRequest(streamSlug, new File(resultUri.getPath()));
        mCompositeSubscription.add(AppsterWebServices.get().saveFirstStreamImage("Bearer " +
                AppsterApplication.mAppPreferences.getUserToken(), request.build())
                .filter(booleanBaseResponse -> isFragmentUIActive())
                .subscribe(streamPostImageResponse -> {
                    if (streamPostImageResponse.getData()) {
                        Toast.makeText(getContext().getApplicationContext(),
                                getString(R.string.cover_image_changed), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Timber.e(error.getMessage())));
    }

    //endregion
}
