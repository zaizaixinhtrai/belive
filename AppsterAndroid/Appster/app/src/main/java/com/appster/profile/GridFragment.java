package com.appster.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.PostDetailActivity;
import com.appster.adapters.GirdViewUserPostAdapter;
import com.appster.comments.ItemClassComments;
import com.appster.fragment.BaseVisibleItemFragment;
import com.appster.models.DeletePostEventModel;
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
import com.appster.viewholder.OnItemClickListener;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GetListPostByUserRequestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.LogUtils;
import com.apster.common.UiUtils;
import com.apster.common.view.CustomScrollListener;
import com.google.android.material.appbar.AppBarLayout;
import com.pack.utility.CheckNetwork;
import com.stickyheaders.PagedLoadScrollListener;
import com.stickyheaders.StickyHeaderLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 14/12/2016.
 */

public class GridFragment extends BaseVisibleItemFragment implements AppBarLayout.OnOffsetChangedListener,
        OnItemClickListener {
    private final int TYPE_GRID = 1;
    private final int GRID_COLUMN_COUNT = 3;

    public static final String BUNDLE_USER_ID = "user_id";
    public static final String BUNDLE_USER_NAME = "BUNDLE_USER_NAME";

    @Bind(R.id.no_data)
    TextView noDataView;
    @Bind(R.id.rv_posts)
    RecyclerView recyclerView;

    private GirdViewUserPostAdapter gridAdapter;
    private GridLayoutManager gridLayoutManager;
    private StickyHeaderLayoutManager linearLayoutManager;
    private UiUtils.GridSpacingItemDecoration gridSpacingItemDecoration;

    private UserProfileView userProfileView;
    private UserModel userProfileDetails;
    private ArrayList<UserPostModel> arrayListProfileGrid = new ArrayList<>();

    private int nextIndexGrid = 0;
    private boolean isLoading;
    private boolean isRefresh;

    private int userID;
    private String mUserName;
    private boolean isOwner;
    private boolean isEndGrid;
    private boolean finishLoadData = false;

    private boolean isNewPost = false;
    private boolean isChangeProfileImage = false;
    PagedLoadScrollListener.LoadCompleteNotifier loadCompleteNotifier;
    private AtomicBoolean mIsAbleHandleRecyclerViewScrolling = new AtomicBoolean(false);

    public static GridFragment getInstance(int userID, String userName) {
        GridFragment f = new GridFragment();
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
        setHasOptionsMenu(true);
        mRootView = inflater.inflate(R.layout.fragment_me_grid_post, container, false);
        ButterKnife.bind(this, mRootView);

        String ownerId = "";
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            ownerId = AppsterApplication.mAppPreferences.getUserModel().getUserId();
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
        setupRecyclerView();
        getPostByUser(false);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNewPost) {
            refreshData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(userID)
                    && _areLecturesLoaded && isVisibleToUser && finishLoadData) {

                if (userProfileView != null) {
                    userProfileView.updateFollowCount();
                }
            }
        }

        if (isVisibleToUser && isFragmentUIActive() && AppsterApplication.mAppPreferences.getIsRefreshGridAndList()) {
            refreshData();
            AppsterApplication.mAppPreferences.setIsRefreshGridAndList(false);
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (data instanceof UserPostModel && userProfileDetails != null) {
            UserPostModel itemFeed = (UserPostModel) data;
            if (itemFeed.getType() == Constants.LIST_USER_POST_NOMAL) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.push_in_to_right, R.anim.push_in_to_left);
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, String.valueOf(itemFeed.getPost().getPostId()));
                intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, String.valueOf(userProfileDetails.getUserId()));
                intent.putExtra(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, position);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_POST_DETAIL, options.toBundle());
            } else {
                if (itemFeed.getStream().isIsRecorded() && (itemFeed.getStream().getStatus() == Constants.StreamStatus.StreamEnd)) {
                    ((BaseToolBarActivity) getActivity()).openViewLiveStream(itemFeed.getStream().getStreamRecording().getDownloadUrl(),
                            itemFeed.getStream().getSlug(), userProfileDetails.getUserImage(), true);
                } else {
                    ((BaseToolBarActivity) getActivity()).openViewLiveStream(itemFeed.getStream().getStreamUrl(),
                            itemFeed.getStream().getSlug(), userProfileDetails.getUserImage(), false);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
            arrayListProfileGrid.remove(position);
            gridAdapter.notifyItemRemoved(position);
        }
    }

    /**
     * @return the position of item in {@link #arrayListProfileGrid} which has the passed id
     * if there no item has the id then -1 will be returned;
     */
    private int getItemPositionById(String id, boolean isStream) {
        if (arrayListProfileGrid == null || arrayListProfileGrid.isEmpty() || TextUtils.isEmpty(id)) {
            return -1;
        }
        for (int i = 0; i < arrayListProfileGrid.size(); i++) {
            if (isStream) {
                StreamModel item = arrayListProfileGrid.get(i).getStream();
                if (item != null && id.equals(item.getSlug())) {
                    return i;
                }
            } else {
                ItemModelClassNewsFeed item = arrayListProfileGrid.get(i).getPost();
                if (item != null && id.equals(item.getPostId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void setupRecyclerView() {

        linearLayoutManager = new StickyHeaderLayoutManager();
        gridLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMN_COUNT);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return gridAdapter.isViewProgress(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });

        int space = (int) getResources().getDimension(R.dimen.item_offset);
        gridSpacingItemDecoration = new UiUtils.GridSpacingItemDecoration(GRID_COLUMN_COUNT, space, true);
        gridSpacingItemDecoration.setNoTopEdge(true);

        if (gridAdapter == null) {
            gridAdapter = new GirdViewUserPostAdapter(recyclerView, getActivity(), arrayListProfileGrid);
            gridAdapter.setUserProfileDetails(userProfileDetails);
            gridAdapter.setOnItemClickListener(this);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(gridSpacingItemDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(gridAdapter);
        recyclerView.addOnScrollListener(new PagedLoadScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, LoadCompleteNotifier loadComplete) {
                loadCompleteNotifier = loadComplete;
                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    if (!isEndGrid) {
                        loadMore();
                    }

                } else {
                    ((BaseActivity) getActivity()).utility.showMessage("", getActivity().getString(R.string.no_internet_connection), getActivity());
                }
            }
        });

        gridAdapter.setOnLoadMoreListener(() -> {
            if (isEndGrid || !CheckNetwork.isNetworkAvailable(getActivity())) {
                return;
            }
            gridAdapter.addProgressItem();

            Handler handler = new Handler();
            handler.postDelayed(this::loadMore, gridAdapter.getTimeDelay());
        });

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

    public void setUserProfileView(UserProfileView userProfileView) {
        if (this.userProfileView == null) {
            this.userProfileView = userProfileView;
        }
    }

    public void setUserProfileDetails(UserModel userProfileDetails) {
        if (this.userProfileDetails == null) {
            this.userProfileDetails = userProfileDetails;
        }
    }

    private void notifyDataSetChanged() {
//        listAdapter.setItemsAndNotify(arrayListProfileList);
        gridAdapter.setItemsAndNotify(arrayListProfileGrid);
    }

    private void loadMore() {
        isLoading = true;
        LogUtils.logE("*****", "** grid load more + " + arrayListProfileGrid.size());
        getPostByUser(false);
    }

    public void refreshData() {
        isLoading = true;
        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            nextIndexGrid = 0;
            isEndGrid = false;
            isRefresh = true;
            getPostByUser(false);
        } else {
            isLoading = false;
        }
    }

    //=========== inner methods ====================================================================
    public void setNewPost(boolean newPost) {
        isNewPost = newPost;
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

    public void updateGrid(Intent data) {

        if (data != null) {

            boolean isDelete = data.getBooleanExtra(ConstantBundleKey.BUNDLE_DELETE_POST_ABLE, false);

            if (isDelete) {

                int position = data.getIntExtra(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, 0);
                String idPost = arrayListProfileGrid.get(position).getPost().getPostId();

                if (arrayListProfileGrid.size() > position) {

                    arrayListProfileGrid.remove(position);
                    gridAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }

                ListenerEventModel listenerEventModel = new ListenerEventModel();
                listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.DELETE_POST);
                listenerEventModel.setTypeFragment(ListenerEventModel.TypeFragment.PROFILE_ME);
                DeletePostEventModel deletePostEventModel = new DeletePostEventModel();
                deletePostEventModel.setPostId(idPost);
                listenerEventModel.setDeletePostEventModel(deletePostEventModel);
                ((BaseActivity) getActivity()).eventChange(listenerEventModel);

            } else {
                refreshData();
            }
        }

    }

    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.DELETE_POST) {
//            removeListAfterDeleteFromGrid(listenerEventModel.getDeletePostEventModel().getPostId());
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    private void updateLike(ListenerEventModel listenerEventModel) {

//        if (arrayListProfileList == null) {
//            return;
//        }
//
//        for (int i = 0; i < arrayListProfileList.size(); i++) {
//            if (arrayListProfileList.get(i).getPost().getPostId().equals(listenerEventModel.getNewLikeEventModel().getPostId())) {
//                arrayListProfileList.get(i).getPost().setIsLike(listenerEventModel.getNewLikeEventModel().getIsLike());
//                arrayListProfileList.get(i).getPost().setLikeCount(listenerEventModel.getNewLikeEventModel().getLikeCount());
//                break;
//            }
//        }
//
//        listAdapter.notifyAllSectionsDataSetChanged();
    }

    void removePostOnGird(String postId) {
        if (arrayListProfileGrid == null && TextUtils.isEmpty(postId)) {
            return;
        }

        for (int i = 0; i < arrayListProfileGrid.size(); i++) {
            if (arrayListProfileGrid.get(i).getPost() != null && arrayListProfileGrid.get(i).getPost().getPostId().equals(postId)) {
                arrayListProfileGrid.remove(i);
                gridAdapter.notifyItemRemoved(i);
                checkIfNoData();
                break;
            }
        }
    }

    private void getDataFromCommentClass(ListenerEventModel listenerEventModel) {
//        if (arrayListProfileList == null) {
//            return;
//        }
//
//        for (int i = 0; i < arrayListProfileList.size(); i++) {
//            if (arrayListProfileList.get(i).getType() == Constants.LIST_USER_POST_NOMAL &&
//                    arrayListProfileList.get(i).getPost().getPostId().equals(listenerEventModel.getNewCommentEventModel().getPostId())) {
//                arrayListProfileList.get(i).getPost().getCommentList().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
//                break;
//            }
//        }
//
//        listAdapter.notifyAllSectionsDataSetChanged();
    }

    private void getPostByUser(final boolean isShowingDialog) {

        if (!DialogManager.isShowing() && isShowingDialog) {
            DialogManager.getInstance().showDialog(getActivity(), getString(R.string.connecting_msg));
        }

        GetListPostByUserRequestModel requestModel = new GetListPostByUserRequestModel();
        requestModel.setProfileId(userID);
        requestModel.setUserName(mUserName);
        requestModel.setViewType(TYPE_GRID);
        requestModel.setLimit(Constants.ITEM_PAGE_LIMITED_FOR_GRID);
        requestModel.setNextId(nextIndexGrid);

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
//                        else{
//                            updateDataInBackGround(getListPostByUserDataResponseModel, viewType);
//                        }
                    finishLoadData = true;
                }, error -> {
                    if (DialogManager.isShowing()) {
                        DialogManager.getInstance().dismisDialog();
                    }
                    isLoading = false;
                    Timber.e(error.getMessage());
                    onErrorWebServiceCall(error.getMessage(), Constants.RETROFIT_ERROR);

                    gridAdapter.removeProgressItem();
                    gridAdapter.setLoaded();
                    gridAdapter.notifyDataSetChanged();
                }));

    }


    private void updateDataInBackGround(BaseResponse<BaseDataPagingResponseModel<UserPostModel>> data, int viewType) {
        if (data.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            return;
        }
        if (data.getData().getResult() != null) {
            arrayListProfileGrid.addAll(data.getData().getResult());
        }
        isEndGrid = data.getData().isEnd();
        nextIndexGrid = data.getData().getNextId();
    }

    private void updateUI(BaseResponse<BaseDataPagingResponseModel<UserPostModel>> userProfileResponseModel, boolean isShowingDialog) {
        if (userProfileResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            removeLoadView();
            return;
        }

        if (isRefresh) {
            arrayListProfileGrid.clear();
            isRefresh = false;
        }

        gridAdapter.removeProgressItem();
        if (userProfileResponseModel.getData().getResult() != null) {
            arrayListProfileGrid.addAll(userProfileResponseModel.getData().getResult());
            gridAdapter.setLoaded();
            gridAdapter.notifyDataSetChanged();
        }

        isEndGrid = userProfileResponseModel.getData().isEnd();
        nextIndexGrid = userProfileResponseModel.getData().getNextId();

        if (isNewPost) {
            isNewPost = false;
            if (recyclerView != null) {
                recyclerView.scrollToPosition(0);
            }
        }
        checkIfNoData();
    }

    private void checkIfNoData() {
        if (noDataView == null) return;
        if (arrayListProfileGrid == null || arrayListProfileGrid.size() <= 0) {
            noDataView.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.GONE);
        }
    }

    private void removeLoadView() {
        gridAdapter.removeProgressItem();
        gridAdapter.setLoaded();
    }

    public void onScrollUpListView() {
        scrollTopUpRecyclerView(recyclerView, false);
    }
}
