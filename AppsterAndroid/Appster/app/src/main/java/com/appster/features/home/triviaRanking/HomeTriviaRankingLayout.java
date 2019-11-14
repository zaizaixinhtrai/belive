package com.appster.features.home.triviaRanking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appster.R;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView;
import com.appster.features.home.triviaRanking.delegates.TopTriviaRankingAdapter;
import com.appster.features.mvpbase.RecyclerItemCallBack;
import com.appster.features.stream.TriviaRankingLayout;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DiffCallBaseUtils;
import com.data.repository.TriviaDataRepository;
import com.data.repository.datasource.cloud.CloudTriviaDataSource;
import com.domain.interactors.trivia.TriviaRankingListUseCase;
import com.domain.models.WinnerModel;
import com.domain.repository.TriviaRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by thanhbc on 5/18/18.
 */

public class HomeTriviaRankingLayout extends FrameLayout implements OnLoadMoreListenerRecyclerView {

    @Bind(R.id.rcvWinnerList)
    LoadMoreRecyclerView rcvWinnerList;
    TriviaRankingListUseCase topWinnerUseCase;
    @Bind(R.id.tvNodata)
    TextView tvNodata;
    private TopTriviaRankingAdapter mTopWinnerAdapter;
    protected CompositeSubscription mCompositeSubscription;
    List<DisplayableItem> mWinnerItems;

    @TriviaRankingLayout.Mode
    private int mMode;
    private int mNextId;
    private boolean isEnd;

    RecyclerItemCallBack<WinnerModel> mRecyclerItemCallBack;



    public HomeTriviaRankingLayout(@NonNull Context context, @TriviaRankingLayout.Mode int mode, RecyclerItemCallBack<WinnerModel> rankingItemCallback) {
        super(context);
        this.mMode = mode;
        mRecyclerItemCallBack = rankingItemCallback;
        constructor();
    }

    public HomeTriviaRankingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeTriviaRankingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    private void constructor() {
        inflateViews();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        loadRankingList();
    }

    private void inflateViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.top_winner_layout_item, this, true);
        ButterKnife.bind(this);
    }

    private void loadRankingList() {
        String authen = AppsterUtility.getAuth();
        AppsterWebserviceAPI mService = AppsterWebServices.get();
        TriviaRepository triviaDataSource = new TriviaDataRepository(new CloudTriviaDataSource(mService, authen));
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        topWinnerUseCase = new TriviaRankingListUseCase(uiThread, ioThread, triviaDataSource);
        DialogManager.getInstance().showDialog(getContext(), getContext().getString(R.string.connecting_msg));
        mCompositeSubscription.add(topWinnerUseCase.execute(TriviaRankingListUseCase.Params.byRankingType(mMode, mNextId, Constants.PAGE_LIMITED))
                .subscribe(winnerModels -> {
                            setAdapter(winnerModels.data);
                            mNextId = winnerModels.nextId;
                            isEnd = winnerModels.isEnd;
                        },
                        error -> {
                            Timber.e(error.getMessage());
                            DialogManager.getInstance().dismisDialog();
                        }));
    }

    private void setAdapter(List<DisplayableItem> listUser) {
        DialogManager.getInstance().dismisDialog();
        mWinnerItems = new ArrayList<>(listUser);
        mTopWinnerAdapter = new TopTriviaRankingAdapter(new DiffCallBaseUtils(), listUser,mRecyclerItemCallBack);
        rcvWinnerList.setOnLoadMoreListener(this);
        rcvWinnerList.setAdapter(mTopWinnerAdapter);
        checkEmptyData();
    }

    @Override
    public void onLoadMore() {
        if (isEnd) return;
        if(rcvWinnerList!=null) rcvWinnerList.post(() -> mTopWinnerAdapter.addLoadMoreItem());
        mCompositeSubscription.add(topWinnerUseCase.execute(TriviaRankingListUseCase.Params.byRankingType(mMode, mNextId, Constants.PAGE_LIMITED))
                .subscribe(winnerModels -> {
                            addListUser(winnerModels.data);
                            mNextId = winnerModels.nextId;
                            isEnd = winnerModels.isEnd;
                        },
                        error -> {
                            Timber.e(error.getMessage());
                            rcvWinnerList.setLoading(false);
                        }));
    }

    private void checkEmptyData() {
        if (mWinnerItems == null || mWinnerItems.isEmpty()) {
            tvNodata.setVisibility(VISIBLE);
            rcvWinnerList.setVisibility(GONE);
        } else {
            tvNodata.setVisibility(GONE);
            rcvWinnerList.setVisibility(VISIBLE);
        }
    }

    private void addListUser(List<DisplayableItem> listUser) {
        if (listUser != null && !listUser.isEmpty() && mTopWinnerAdapter!=null) {
            mWinnerItems.addAll(listUser);
            mTopWinnerAdapter.updateItems(mWinnerItems);
        }
        rcvWinnerList.setLoading(false);
    }
}