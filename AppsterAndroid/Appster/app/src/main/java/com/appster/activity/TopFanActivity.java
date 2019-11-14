package com.appster.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.customview.CircleImageView;
import com.appster.layout.TopPanLayout;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.TopFanModel;
import com.appster.models.UserModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

/**
 * Created by User on 8/31/2016.
 */
public class TopFanActivity extends BaseToolBarActivity {

    private FrameLayout topPanContent;
    private TopPanLayout topPanLayout;
    UserModel userProfileDetails;
    private ArrayList<TopFanModel> watchers = new ArrayList<>();
    private ImageView userImage1;
    private ImageView userImage2;
    private ImageView userImage3;
    private TextView totalStars;
    private Subscription subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data
        Intent intent = getIntent();
        if (intent != null) {
            userProfileDetails = intent.getParcelableExtra(ConstantBundleKey.BUNDLE_PROFILE);
            totalStars = (TextView) findViewById(R.id.totalStars);
            totalStars.setTypeface(Typeface.DEFAULT_BOLD);

            if (userProfileDetails != null) {
                getCreditUser(userProfileDetails.getUserId());
            }
        }

        if (userProfileDetails != null) {
            topPanLayout = new TopPanLayout(this, userProfileDetails, false, false, false);
            topPanLayout.setGetTopPanModelsListner(new TopPanLayout.GetTopPanModelsListner() {
                @Override
                public void onGetTopPanModels(List<TopFanModel> topFanModels) {
                    watchers.clear();
                    watchers.addAll(topFanModels);
                    setTopFans();
                }

                @Override
                public void finishGetTopFan() {

                }
            });
            topPanContent.addView(topPanLayout);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    private void getCreditUser(String userId) {
        RxUtils.unsubscribeIfNotNull(subscription);
        subscription = AppsterWebServices.get().getCredit(userId)
                .onErrorResumeNext(Observable::error)
                .subscribe(dataResponse -> {
                    if (dataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        totalStars.setText(String.valueOf(dataResponse.getData().getTotalGoldFans()));
                        if (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(userId)) {
                            AppsterApplication.mAppPreferences.getUserModel().setTotalBean(dataResponse.getData().getTotalBean());
                            AppsterApplication.mAppPreferences.getUserModel().setTotalGold(dataResponse.getData().getTotalGold());
                            AppsterApplication.mAppPreferences.getUserModel().setTotalGoldFans(dataResponse.getData().getTotalGoldFans());
                        }
                    } else {
                        handleError(dataResponse.getMessage(), dataResponse.getCode());
                    }
                }, error -> handleError(error.getMessage(), Constants.RETROFIT_ERROR));
    }

    private void setTopFans() {

        if (watchers == null || watchers.size() == 0) {
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage1);
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage2);
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage3);
            return;
        }


        if (watchers.size() == 1) {
            ImageLoaderUtil.displayUserImage(this, watchers.get(0).getUserImage(), userImage1);
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage2);
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage3);

        } else if (watchers.size() == 2) {
            ImageLoaderUtil.displayUserImage(this, watchers.get(0).getUserImage(), userImage1);
            ImageLoaderUtil.displayUserImage(this, watchers.get(1).getUserImage(), userImage2);
            ImageLoaderUtil.displayUserImage(this, R.drawable.user_image_default, userImage3);

        } else if (watchers.size() >= 3) {
            ImageLoaderUtil.displayUserImage(this, watchers.get(0).getUserImage(), userImage1);
            ImageLoaderUtil.displayUserImage(this, watchers.get(1).getUserImage(), userImage2);
            ImageLoaderUtil.displayUserImage(this, watchers.get(2).getUserImage(), userImage3);
        }
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_toppan;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.toppan_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void init() {
        topPanContent = (FrameLayout) findViewById(R.id.toppan_list);
        userImage1 = (CircleImageView) findViewById(R.id.userImage1);
        userImage2 = (CircleImageView) findViewById(R.id.userImage2);
        userImage3 = (CircleImageView) findViewById(R.id.userImage3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
            return;

        switch (requestCode) {
            case Constants.REQUEST_CODE_VIEW_USER_PROFILE:
                if (resultCode == RESULT_OK && data != null) {

                    FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                            (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);
                    if (followStatusChangedEvent != null && topPanLayout != null) {
                        topPanLayout.changeFollowUser(followStatusChangedEvent.getUserId(), followStatusChangedEvent.getFollowType());
                    }

                    boolean isSomeOneBlocked = data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false);
                    if (isSomeOneBlocked) {
                        getCreditUser(userProfileDetails.getUserId());
                        topPanLayout.refreshTopFan();
                        Intent intent = getIntent();
                        intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
                        setResult(Activity.RESULT_OK, intent);
                    }
                }
        }
    }
}
