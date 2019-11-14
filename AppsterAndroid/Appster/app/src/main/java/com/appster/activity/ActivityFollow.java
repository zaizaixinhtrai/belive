package com.appster.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appster.R;
import com.appster.adapters.FollowRecyclerAdapter;
import com.appster.data.AppPreferences;
import com.appster.models.FollowItemModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.NotificationModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.FollowRequestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.UiUtils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by User on 9/28/2015.
 */
public class ActivityFollow extends BaseToolBarActivity {

    private RecyclerView recyclerView;
    @Bind(R.id.txt_empty)
    TextView txtEmpty;

    private int index;
    private String profile_id;
    private TypeList type = TypeList.FOLLOWER;
    private AppPreferences mAppPreferences;

    //    private AdapterFollowUser adapter;
    private ArrayList<FollowItemModel> arrFollowers;
    private boolean isTheEnd = false;

    FollowRecyclerAdapter adapterFollowTemp;
    private SwipeRefreshLayout swiperefresh;
    private Subscription subscription;

    public enum TypeList {
        FOLLOWER,
        FOLLOWING
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int temp = extras.getInt(ConstantBundleKey.BUNDLE_TYPE_KEY);

            type = TypeList.values()[temp];
            profile_id = extras.getString(ConstantBundleKey.BUNDLE_PROFILE_ID_KEY);
            setTopBarTile(type == TypeList.FOLLOWER ? getString(R.string.profile_followers_title) : getString(R.string.profile_following_title));
        }

        mAppPreferences = new AppPreferences(ActivityFollow.this);
        utility = new DialogInfoUtility();
        getData(true);

    }

    @Override
    public int getLayoutContentId() {
        return R.layout.followers;
    }

    @Override
    public void init() {

        goneNotify(true);

        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.follower_list);
        // Set adapter
        arrFollowers = new ArrayList<>();

        adapterFollowTemp = new FollowRecyclerAdapter(ActivityFollow.this, recyclerView, arrFollowers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int space = (int) getResources().getDimension(R.dimen.chat_list_divider);
        recyclerView.addItemDecoration(new UiUtils.ListSpacingItemDecoration(space, false));
        recyclerView.setAdapter(adapterFollowTemp);

        UiUtils.setColorSwipeRefreshLayout(swiperefresh);
        swiperefresh.setOnRefreshListener(this::refreshData);

        adapterFollowTemp.setOnLoadMoreListener(() -> {
            if (isTheEnd) {
                return;
            }

            adapterFollowTemp.addProgressItem();

            Handler handler = new Handler();
            handler.postDelayed(() -> getData(false), adapterFollowTemp.getTimeDelay());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        handleTurnoffMenuSliding();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
            return;

        if (requestCode == Constants.REQUEST_CODE_VIEW_USER_PROFILE) {
            callChangeEventFromUserProfile(data);

            if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                refreshData();
                Intent intent = getIntent();
                intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
                setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        RxUtils.unsubscribeIfNotNull(subscription);
        subscription = null;

        super.onDestroy();
    }

    void refreshData() {
        if (!CheckNetwork.isNetworkAvailable(ActivityFollow.this)) {
            toastTextOnTheEndListListener("");
            return;
        }

        index = 0;
        getData(false);
    }

    private void callChangeEventFromUserProfile(Intent data) {

        if (data == null) {
            return;
        }

        FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);

        if (followStatusChangedEvent == null) return;

        for (int i = 0; i < arrFollowers.size(); i++) {
            if (arrFollowers.get(i).getUserId().equals(followStatusChangedEvent.getUserId())) {
                arrFollowers.get(i).setIsFollow(followStatusChangedEvent.getFollowType());
                adapterFollowTemp.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }

    public void onClickBack(View view) {
        finish();
    }

    private void getData(boolean isShowDialog) {
        if (type == TypeList.FOLLOWER) {
            getFollowers(isShowDialog);
        } else {
            getFollowing(isShowDialog);
        }
    }


    private void getFollowers(boolean isShowDialog) {

        if (isShowDialog) {
            DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        }
        FollowRequestModel request = new FollowRequestModel();

        request.setProfile_id(profile_id);
        request.setNextId(index);
        request.setLimit(Constants.PAGE_LIMITED);

        subscription = AppsterWebServices.get().getFollowersUsers("Bearer " + mAppPreferences.getUserToken(), request)
                .subscribe(followResponseModel -> {
                    DialogManager.getInstance().dismisDialog();
                    adapterFollowTemp.removeProgressItem();
                    swiperefresh.setRefreshing(false);

                    if (followResponseModel == null) return;
                    if (followResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        progressDataReturn(followResponseModel.getData(), followResponseModel.getCode());
                    } else {
                        handleError(followResponseModel.getMessage(), followResponseModel.getCode());
                    }

                    adapterFollowTemp.setLoaded();
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    swiperefresh.setRefreshing(false);
                    adapterFollowTemp.removeProgressItem();
                    adapterFollowTemp.setLoaded();
                });

    }

    private void getFollowing(boolean isShowDialog) {
        if (isShowDialog) {
            DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        }

        FollowRequestModel request = new FollowRequestModel();
        request.setProfile_id(profile_id);
        request.setNextId(index);
        request.setLimit(Constants.PAGE_LIMITED);

        subscription = AppsterWebServices.get().getFollowingUsers("Bearer " + mAppPreferences.getUserToken(), request)
                .subscribe(followResponseModel -> {

                    DialogManager.getInstance().dismisDialog();
                    adapterFollowTemp.removeProgressItem();
                    swiperefresh.setRefreshing(false);
                    if (followResponseModel == null) return;
                    if (followResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        progressDataReturn(followResponseModel.getData(), followResponseModel.getCode());
                    } else {
                        handleError(followResponseModel.getMessage(), followResponseModel.getCode());
                    }

                    adapterFollowTemp.setLoaded();
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    swiperefresh.setRefreshing(false);
                    adapterFollowTemp.removeProgressItem();
                    adapterFollowTemp.setLoaded();
                });

    }

    private void progressDataReturn(BaseDataPagingResponseModel<FollowItemModel> followResponseModel, int code) {

        if (index == 0 && !arrFollowers.isEmpty()
                && followResponseModel.getResult() != null
                && !followResponseModel.getResult().isEmpty()) {
            arrFollowers.clear();
            arrFollowers.addAll(followResponseModel.getResult());
        } else if (followResponseModel.getResult() != null
                && !followResponseModel.getResult().isEmpty()) {
            arrFollowers.addAll(followResponseModel.getResult());
        }

        if (code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            index = followResponseModel.getNextId();
            isTheEnd = followResponseModel.isEnd();
        }

        if (txtEmpty != null) {
            if (arrFollowers.isEmpty()) {
                txtEmpty.setVisibility(View.VISIBLE);
            } else {
                txtEmpty.setVisibility(View.INVISIBLE);
            }
        }

        adapterFollowTemp.notifyDataSetChanged();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NotificationModel.NotificationEntity entity) {
        redirectNotificationShowing(entity);
    }

}
