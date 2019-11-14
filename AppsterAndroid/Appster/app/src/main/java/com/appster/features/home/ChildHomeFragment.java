package com.appster.features.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView;
import com.appster.features.banner_detail.BannerDetailActivity;
import com.appster.features.category_detail.CategoryDetailActivity;
import com.appster.features.home.viewholders.CategoriesViewHolder;
import com.appster.features.home.viewholders.HomeBannerItemViewHolder;
import com.appster.features.home.viewholders.HomeItemViewHolder;
import com.appster.fragment.BaseFragment;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.models.HomeCurrentEventModel;
import com.appster.models.HomeItemModel;
import com.appster.models.TagListLiveStreamModel;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.models.event_bus_models.EventBusRefreshHomeTab;
import com.appster.tracking.EventTracker;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.apster.common.LogUtils;
import com.apster.common.UiUtils;
import com.apster.common.Utils;
import com.pack.utility.CheckNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import static com.apster.common.Constants.REQUEST_CATEGORY_DETAIL_ACTIVITY;

public class ChildHomeFragment extends BaseFragment implements
        HomeItemViewHolder.OnClickListener,
        HomeBannerItemViewHolder.OnClickListener,
        CategoriesViewHolder.OnClickListener,
        OnLoadMoreListenerRecyclerView,
        HomeContract.View {

    protected static final String CATEGORY_ID = "categoryId";
    protected static final int GRID_COLUMN_COUNT = 2;

    LoadMoreRecyclerView mLoadMoreRecyclerView;
    SwipeRefreshLayout swipeRefreshlayout;

    List<DisplayableItem> mHomeItems;
    HomeScreenAdapter mHomeScreenAdapter;
    private boolean shouldRefresh;

    private int typeFeed;
    private int offset;
    private boolean isEnd;
    AtomicBoolean mIsRefresing = new AtomicBoolean(false);
    GridLayoutManager mGridLayoutManager;
    protected ItemOffsetDecoration mItemDecoration;
    protected String mAuthen;
    private HomeContract.UserActions mPresenter;
    protected boolean mShouldShowDialog;

    //region view contract
    @Override
    public Context getViewContext() {
        return getActivity();
    }

    @Override
    public void loadError(String errorMessage, int code) {
        onErrorWebServiceCall(errorMessage, code);
    }

    @Override
    public void showProgress() {
        if (mShouldShowDialog) showDialog();
    }

    @Override
    public void hideProgress() {
        if (mShouldShowDialog) dismissDilaog();
    }

    @Override
    public void onCategoriesReceived(List<TagListLiveStreamModel> categories) {
        mHomeItems.add(new StreamCategory(categories));
        if (mItemDecoration != null) mItemDecoration.setContainCategoriesItem(true);
        mPresenter.getStreamsByTag(typeFeed, offset);
    }

    @Override
    public void onEventsReceived(List<HomeCurrentEventModel> events) {
        if (events != null) {
            setupBanners(events);
        }
        if (shouldGetCategories()) {
            mPresenter.getCategoriesByTag(typeFeed);
        } else {
            mPresenter.getStreamsByTag(typeFeed, offset);
        }
    }

    public boolean shouldGetCategories() {
        return typeFeed == Constants.HOME_TAG_ID_HOT;
    }

    @Override
    public void onStreamsReceived(List<HomeItemModel> streams, int nextId, boolean isEnd) {
        mHomeScreenAdapter.removeLoadingItem();
        handleDataPopularByTag(streams);
        this.offset = nextId;
        this.isEnd = isEnd;
        AppsterUtility.resetPrefListByKey(getContext(), Constants.STREAM_BLOCKED_LIST, AppsterApplication.mAppPreferences.getUserId());
    }

    @Override
    public void refreshCompleted() {
        if (mHomeScreenAdapter != null) mHomeScreenAdapter.removeLoadingItem();
        if (swipeRefreshlayout != null) {
            swipeRefreshlayout.setRefreshing(false);
            mIsRefresing.set(false);
        }
    }

    @Override
    public boolean isRunning() {
        return isFragmentUIActive();
    }

    //endregion

    public interface PagerChangeRequestListener {
        void onRequestPagerChange(String pageId);
    }

    public static ChildHomeFragment newInstance(int categoryId) {
        ChildHomeFragment f = new ChildHomeFragment();
        Bundle b = new Bundle();
        b.putInt(CATEGORY_ID, categoryId);
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeFeed = getArguments().getInt(CATEGORY_ID);
        mAuthen = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
        mPresenter = getPresenter();
        mPresenter.attachView(this);
    }

    protected HomeContract.UserActions getPresenter() {
        return new HomePresenter(AppsterWebServices.get(), mAuthen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logE("refresh", "** onCreateView");
        if (mRootView != null) {
            return mRootView;
        }

        // Pass your layout xml to the inflater and assign it to rootView.
        mRootView = inflater.inflate(R.layout.fragment_child_home, container, false);
        swipeRefreshlayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshlayout);
        mLoadMoreRecyclerView = (LoadMoreRecyclerView) mRootView.findViewById(R.id.listview);
        init();
        setAdapter();
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !_areLecturesLoaded && isResumed()) {
            if (CheckNetwork.isNetworkAvailable(getActivity())) {
                pullData(true);
                _areLecturesLoaded = true;
            } else {
                ((BaseActivity) getActivity()).utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), getActivity());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.e("onResume");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (getUserVisibleHint() && !_areLecturesLoaded) {
            setUserVisibleHint(true);
        }
        if (shouldRefresh) {
            Timber.e("refresh by onResume");
            onRefreshData();
            shouldRefresh = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mPresenter.detachView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBusRefreshFragment event) {
        if (AppsterApplication.mAppPreferences.getCurrentTagOnHome() == typeFeed) {
            Timber.e("refresh by tap home");
        } else {
            shouldRefresh = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFragment(EventBusRefreshHomeTab eventBusdata) {
        if (isFragmentUIActive()) {
            Timber.e("refresh by RefreshData");
            onRefreshData();
        } else {
            shouldRefresh = true;
        }
        Timber.d("refresh date by event bus " + typeFeed);
    }

    private void init() {

        swipeRefreshlayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshlayout);

        UiUtils.setColorSwipeRefreshLayout(swipeRefreshlayout);
        swipeRefreshlayout.setOnRefreshListener(this::onRefreshData);
    }

    public void onRefreshDataForTabHot() {

        if (mHomeScreenAdapter != null && mHomeScreenAdapter.getItemCount() > 0 && mGridLayoutManager != null && swipeRefreshlayout != null) {
            swipeRefreshlayout.setRefreshing(true);
        }
        pullData(false);
    }

    public void onRefreshData() {
        if (!CheckNetwork.isNetworkAvailable(getActivity())) {
            ((BaseActivity) getActivity()).utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection),
                    getActivity());
            swipeRefreshlayout.setRefreshing(false);
            return;
        }
        offset = 0;
        pullData(false);
    }

    private void pullData(boolean isShowDialog) {
        if (mIsRefresing.get()) return;
        mIsRefresing.set(true);
        if (mHomeItems != null) {
            mHomeItems.clear();
            //reset item decorator
            if (mItemDecoration != null) {
                mItemDecoration.setContainCategoriesItem(false);
                mItemDecoration.setContainBannerItem(false);
            }

        }
        getCurrentEvent(isShowDialog);
    }

    private void setAdapter() {
        mHomeItems = new ArrayList<>();
        mGridLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMN_COUNT);
        mLoadMoreRecyclerView.setLayoutManager(mGridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_offset);
        mItemDecoration = new ItemOffsetDecoration(GRID_COLUMN_COUNT, spacingInPixels, ContextCompat.getDrawable(getContext(), R.drawable.divider_drawable));
        mLoadMoreRecyclerView.addItemDecoration(mItemDecoration);
        mHomeScreenAdapter = new HomeScreenAdapter(null, new ArrayList<>(), this, this, this, shouldShowCategoryTag(), shouldShowDistance());
        mLoadMoreRecyclerView.setAdapter(mHomeScreenAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                switch (mHomeScreenAdapter.getItemViewType(position)) {
                    case HomeScreenAdapter.LOAD_MORE:
                    case HomeScreenAdapter.BANNER:
                    case HomeScreenAdapter.CATEGORY_ITEMS:
                        return mGridLayoutManager.getSpanCount();
                    default:
                        return 1;
                }
            }
        });
    }

    public boolean shouldShowCategoryTag() {
        return typeFeed == Constants.HOME_TAG_ID_HOT;
    }

    public boolean shouldShowDistance() {
        return false;
    }

    @Override
    public void onLoadMore() {

        if (isEnd) {
            ((BaseActivity) getActivity()).toastTextOnTheEndListListener("");
            return;
        }
        mLoadMoreRecyclerView.post(() -> mHomeScreenAdapter.addLoadMoreItem());
        getPopularByTag(false, offset);

    }


    /**
     * get current event panel as well as voting bar.
     *
     * @param isShowDialog if true the progress will be showed.
     */
    private void getCurrentEvent(final boolean isShowDialog) {
        mShouldShowDialog = isShowDialog;
        if (isShowDialog && !AppsterApplication.mAppPreferences.getFlagNewlyUser()) {
            showProgress();
        }
        mPresenter.getEventsByTag(typeFeed);
    }

    private void setupBanners(List<HomeCurrentEventModel> eventModels) {
        if (!eventModels.isEmpty()) {
            mHomeItems.add(0, new BannerModel(eventModels));
            if (mItemDecoration != null) mItemDecoration.setContainBannerItem(true);
        }
    }

    //region adapter callbacks
    @Override
    public void onHomeItemUserImageClicked(HomeItemModel item, int position) {
        if (item.isIsRecorded() && (item.getStatus() == Constants.StreamStatus.StreamEnd)) {
            ((BaseToolBarActivity) getContext()).openViewLiveStream(item.getStreamRecording().getDownloadUrl(), item.getSlug(), item.getPublisher().getUserImage(), true);
        } else {
            ((BaseToolBarActivity) getContext()).openViewLiveStream(item.getStreamUrl(), item.getSlug(), item.getPublisher().getUserImage(), false);
        }

        //Track Event Category
        EventTracker.trackSelectCategory(item.getTagName());
    }


    @Override
    public void onHomeItemUserNameClicked(HomeItemModel item, int position) {
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            if (!AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(item.getPublisher().getUserId())) {
                ((BaseToolBarActivity) getContext()).startActivityProfile(item.getPublisher().getUserId(),
                        item.getPublisher().getUserName());
            }

        } else {
            ((BaseToolBarActivity) getContext()).startActivityProfile(item.getPublisher().getUserId(),
                    item.getPublisher().getUserName());
        }
    }

    @Override
    public void onBannerItemClicked(HomeCurrentEventModel eventModel) {
        if (eventModel.getActionType() == Constants.EVENT_TYPE_LINK_USER) {
            ((BaseActivity) getContext()).startActivityProfile(eventModel.getActionValue(), "");
        } else if (eventModel.getActionType() == Constants.EVENT_TYPE_LINK_EVENT) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent = BannerDetailActivity.createIntent(getContext(), eventModel.getTitle(), eventModel.getDetailUrl(), eventModel.getActionValue());
            startActivity(intent, options.toBundle());
        } else if (eventModel.getActionType() == Constants.EVENT_TYPE_LINK_CATEGORY) {
            Intent intent = CategoryDetailActivity.createIntent(getContext(), eventModel.getTagId(), eventModel.getTagName());
            if (getActivity() != null)
                getActivity().startActivityForResult(intent, REQUEST_CATEGORY_DETAIL_ACTIVITY);
        }
    }

    @Override
    public void onCategoryItemClicked(TagListLiveStreamModel categoryItem) {
        Intent intent = CategoryDetailActivity.createIntent(getContext(), categoryItem.getTagId(), categoryItem.getTagName());
        if (getActivity() != null)
            getActivity().startActivityForResult(intent, REQUEST_CATEGORY_DETAIL_ACTIVITY);
    }

    //endregion

    /**
     * get data by type. each type Correspondingly one tab in home screen.
     *
     * @param isShowDialog if true the progress will be showed.
     */
    private void getPopularByTag(final boolean isShowDialog, int nextId) {
        mShouldShowDialog = isShowDialog;
        if (isShowDialog && !AppsterApplication.mAppPreferences.getFlagNewlyUser()) {
            showDialog();
        }
        AppsterApplication.mAppPreferences.setFlagNewlyUser(false);
        mPresenter.getStreamsByTag(typeFeed, nextId);
    }

    private void handleDataPopularByTag(List<HomeItemModel> models) {
        // Check if refresh
        mHomeScreenAdapter.removeLoadingItem();
        mHomeItems.addAll(models);
        mHomeScreenAdapter.updateItems(mHomeItems);
        mLoadMoreRecyclerView.setLoading(false);
        if (offset == 0) {
            mGridLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public void onScrollUpListView() {
        scrollTopUpRecyclerView(mLoadMoreRecyclerView, true);
    }

    private static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private final int bannerAndCategoriesSpacing = Utils.dpToPx(9);
        private boolean mIsContainCategoriesItem;
        private boolean mIsContainBannerItem;
        private Drawable mVerticalDivider;
        private Drawable mHorizontalDivider;

        void setContainCategoriesItem(boolean containCategoriesItem) {
            mIsContainCategoriesItem = containCategoriesItem;
        }

        void setContainBannerItem(boolean containBannerItem) {
            mIsContainBannerItem = containBannerItem;
        }


        ItemOffsetDecoration(int spanCount, int spacing, Drawable verticalDivider) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            mVerticalDivider = verticalDivider;
            mHorizontalDivider = verticalDivider;
        }

//        @Override
//        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.Status state) {
//            drawHorizontalDividers(canvas, parent);
//            drawVerticalDividers(canvas, parent);
//        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) return;
            if (parent.getChildViewHolder(view) instanceof HomeBannerItemViewHolder) {
                //check next position is not over list items
                final int nextPos = position + 1;
                if (nextPos < parent.getAdapter().getItemCount() && isCategoryItem(parent.getAdapter().getItemViewType(nextPos))) {
                    outRect.bottom = -bannerAndCategoriesSpacing;
                }
            }
            if (isDecorated(view, parent)) {
                outRect.top = spacing * 2;
                if (isLeftItem(position)) {
                    outRect.right = spacing;
                } else {
                    outRect.left = spacing;
                }
            }
        }

        /**
         * Adds horizontal dividers to a RecyclerView with a GridLayoutManager or
         * its subclass.
         *
         * @param canvas The {@link Canvas} onto which dividers will be drawn
         * @param parent The RecyclerView onto which dividers are being added
         */
        private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
            final int parentTop = parent.getPaddingTop();
            final int parentBottom = parent.getHeight() - parent.getPaddingBottom();

            for (int i = 0; i < spanCount; i++) {
                final View child = parent.getChildAt(i);
                if (isDecorated(child, parent)) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    final int parentLeft = child.getRight() + params.rightMargin;
                    final int parentRight = parentLeft + mHorizontalDivider.getIntrinsicWidth();

                    mHorizontalDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                    mHorizontalDivider.draw(canvas);
                }
            }
        }

        /**
         * Adds vertical dividers to a RecyclerView with a GridLayoutManager or its
         * subclass.
         *
         * @param canvas The {@link Canvas} onto which dividers will be drawn
         * @param parent The RecyclerView onto which dividers are being added
         */
        private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
            final int parentLeft = parent.getPaddingLeft();
            final int parentRight = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                if (isDecorated(child, parent)) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    final int parentTop = child.getBottom() + params.bottomMargin;
                    final int parentBottom = parentTop + mVerticalDivider.getIntrinsicHeight();

                    mVerticalDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                    mVerticalDivider.draw(canvas);
                }
            }
        }

        public boolean isDecorated(View view, RecyclerView parent) {
            if (parent == null || view == null) return false;
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof HomeItemViewHolder;
        }

        private boolean isCategoryItem(int itemViewType) {
            return itemViewType == HomeScreenAdapter.CATEGORY_ITEMS;
        }

        private boolean isLeftItem(int position) {
            return ((mIsContainCategoriesItem ? position : checkPositionIfIncludeBanner(position)) % spanCount) == 0;
        }

        private int checkPositionIfIncludeBanner(int position) {
            return mIsContainBannerItem ? (position + 1) : position;
        }
    }
}