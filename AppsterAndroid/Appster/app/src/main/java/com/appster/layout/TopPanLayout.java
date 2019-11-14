package com.appster.layout;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.adapters.TopPanAdapter;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.models.TopFanModel;
import com.appster.models.UserModel;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.GetTopFanModel;
import com.apster.common.Constants;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.UiUtils;
import com.pack.utility.CheckNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/31/2016.
 */
public class TopPanLayout extends LinearLayout implements DialogUserProfileFragment.UserProfileActionListener {

    private LayoutInflater inflater;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<TopFanModel> mTopFanModels;
    private UserModel userProfileDetails;
    private TopPanAdapter adapter;
    private GetTopPanModelsListner getTopPanModelsListner;
    private TextView textNoFan;
    private SwipeRefreshLayout swipeRefreshlayout;
    private boolean isEnd;
    private int nextIndex = 0;
    private boolean isViewLiveStream;
    private DialogUserProfileFragment.UserProfileActionListener mUserProfileActionListener;
    private boolean isViewer;

    public TopPanLayout(Context context, UserModel userProfileDetails, boolean disableRefreshlayout, boolean isViewLiveStream, boolean isViewer) {
        super(context);
        this.context = context;
        this.userProfileDetails = userProfileDetails;
        this.isViewLiveStream = isViewLiveStream;
        this.isViewer = isViewer;
        inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.top_pan_layout, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        textNoFan = (TextView) findViewById(R.id.textNoFan);
        swipeRefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshlayout);
        UiUtils.setColorSwipeRefreshLayout(swipeRefreshlayout);
        if (disableRefreshlayout) {
            swipeRefreshlayout.setEnabled(false);
            swipeRefreshlayout.setRefreshing(false);
        }

        utilAdapter();

        swipeRefreshlayout.setOnRefreshListener(() -> {
            if (!CheckNetwork.isNetworkAvailable(context)) {
                ((BaseActivity) context).utility.showMessage(
                        context.getString(R.string.app_name),
                        getResources()
                                .getString(
                                        R.string.no_internet_connection),
                        context);

                return;
            }

            refreshTopFan();
        });

        adapter.setOnLoadMoreListener(() -> {

            if (!CheckNetwork.isNetworkAvailable(context)) {

                return;
            }

            if (isEnd) {
                return;
            }

            adapter.addProgressItem();

            Handler handler = new Handler();
            handler.postDelayed(() -> getTopPan(false), adapter.getTimeDelay());
        });


        if (CheckNetwork.isNetworkAvailable(context)) {
            getTopPan(true);
        } else {
            ((BaseActivity) context).utility.showMessage(
                    context.getString(R.string.app_name),
                    getResources()
                            .getString(
                                    R.string.no_internet_connection),
                    context);
        }
    }

    public void setUserProfileActionListener(DialogUserProfileFragment.UserProfileActionListener userProfileActionListener) {
        mUserProfileActionListener = userProfileActionListener;
        if (adapter != null) {
            adapter.setUserProfileActionListener(this);
        }
    }

    public void refreshTopFan() {
        if (mTopFanModels != null) {
            mTopFanModels.clear();
            adapter.notifyDataSetChanged();
        }
        nextIndex = 0;
        getTopPan(false);
    }

    public void setGetTopPanModelsListner(GetTopPanModelsListner getTopPanModelsListner) {
        this.getTopPanModelsListner = getTopPanModelsListner;
    }

    private void utilAdapter() {
        mTopFanModels = new ArrayList<>();
        adapter = new TopPanAdapter(context, recyclerView, mTopFanModels, isViewLiveStream, isViewer);
        adapter.setUserProfileActionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new UiUtils.ListSpacingItemDecoration((int) getResources().getDimension(R.dimen.chat_list_divider), false));
        recyclerView.setAdapter(adapter);

    }

    public void changeFollowUser(String userId, int followType) {
        if (mTopFanModels != null) {
            for (int i = 0; i < mTopFanModels.size(); i++) {
                if (mTopFanModels.get(i).getUserId().equals(userId)) {
                    mTopFanModels.get(i).setIsFollow(followType);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private void getTopPan(final boolean isShowingDialog) {

        if (isShowingDialog) {
            ((BaseActivity) context).showDialog(context, context.getString(R.string.connecting_msg));
        }

        GetTopFanModel request = new GetTopFanModel();
        request.setUserId(userProfileDetails.getUserId());
        request.setNextId(nextIndex);

        AppsterWebServices.get().getTopFan("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(getTopFanDataResponse -> {

                    if (isShowingDialog) {
                        ((BaseActivity) context).dismisDialog();
                    }
                    swipeRefreshlayout.setRefreshing(false);
                    adapter.removeProgressItem();
                    adapter.setLoaded();

                    if (getTopPanModelsListner != null) {
                        getTopPanModelsListner.finishGetTopFan();
                    }

                    if (getTopFanDataResponse == null) return;

                    if (getTopFanDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        if (mTopFanModels != null && mTopFanModels.size() == 0 && getTopPanModelsListner != null) {
                            getTopPanModelsListner.onGetTopPanModels(getTopFanDataResponse.getData().getResult());
                        }
                        nextIndex = getTopFanDataResponse.getData().getNextId();
                        isEnd = getTopFanDataResponse.getData().isEnd();
                        mTopFanModels.addAll(getTopFanDataResponse.getData().getResult());
                        adapter.notifyDataSetChanged();
                    } else {
                        handleErrorCode(getTopFanDataResponse.getCode(), getTopFanDataResponse.getMessage());
                    }

                    if (mTopFanModels.size() == 0) {
                        textNoFan.setVisibility(VISIBLE);
                        recyclerView.setVisibility(GONE);
                    } else {
                        textNoFan.setVisibility(GONE);
                        recyclerView.setVisibility(VISIBLE);
                    }

                }, error -> {
                    if (isShowingDialog) {
                        ((BaseActivity) context).dismisDialog();
                    }
                    swipeRefreshlayout.setRefreshing(false);
                    adapter.removeProgressItem();
                    adapter.setLoaded();

                    if (getTopPanModelsListner != null) {
                        getTopPanModelsListner.finishGetTopFan();
                    }
                });
    }

    public void handleErrorCode(int code, String message) {
        if (context!=null && code == 603) {
                new DialogbeLiveConfirmation.Builder()
                        .title(context.getString(R.string.app_name))
                        .singleAction(true)
                        .message(message)
                        .onConfirmClicked(() -> AppsterApplication.logout(context))
                        .build().show(context);
//            utility.showMessage(getString(R.string.app_name), errorMessage, BaseActivity.this, mclick);
        }
    }

    @Override
    public void onReportUserClick(String userId) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onReportUserClick(userId);
        }
    }

    @Override
    public void onBlockUserClick(String userId, String displayName) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onBlockUserClick(userId, displayName);
        }

    }

    @Override
    public void onMuteUserClick(String userId, String displayName) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onMuteUserClick(userId, displayName);
        }
    }

    @Override
    public void onUnMuteUserClick(String userId, String displayName) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onUnMuteUserClick(userId, displayName);
        }
    }

    @Override
    public void onFollowCountChanged(int count) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onFollowCountChanged(count);
        }
    }

    @Override
    public void onDimissed() {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onDimissed();
        }
    }

    @Override
    public void onChangeFollowStatus(String userId, int status) {
        for (int i = 0; i < mTopFanModels.size(); i++) {
            if (mTopFanModels.get(i).getUserId().equals(userId)) {
                mTopFanModels.get(i).setIsFollow(status);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onVideoCallClicked(String userId, String userName) {
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onVideoCallClicked(userId,userName);
        }
    }

    public interface GetTopPanModelsListner {
        void onGetTopPanModels(List<TopFanModel> topFanModels);

        void finishGetTopFan();
    }
}
