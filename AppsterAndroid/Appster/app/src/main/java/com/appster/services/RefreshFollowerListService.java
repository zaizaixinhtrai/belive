package com.appster.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.appster.AppsterApplication;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.FollowRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.room.FollowingLocalDbRxHelper;

import java.util.Date;

import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by linh on 21/06/2017.
 */

public class RefreshFollowerListService extends IntentService {
    private static final String USER_ID = "USER_ID";
    public static final String LAST_TIME_SYNC_FOLLOWER_LIST = "LAST_TIME_SYNC_FOLLOWER_LIST";
    private static final long ONE_DAY = 86400000;//one day
    private static final long ONE_MINUTE = 60000;// one minute

    private String mUserId;
    CompositeSubscription mCompositeSubscription;

    public static Intent createIntent(Context context, String userId) {
        Intent intent = new Intent(context, RefreshFollowerListService.class);
        intent.putExtra(USER_ID, userId);
        return intent;
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RefreshFollowerListService() {
        super("RefreshFollowerListService");
        Timber.d("RefreshFollowerListService constructor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("onHandleIntent");
        if (intent == null) {
            return;
        }
        mUserId = intent.getStringExtra(USER_ID);
        Timber.d("onHandleIntent %s",mUserId);
        if (TextUtils.isEmpty(mUserId)) {
            return;
        }
        Timber.d("onHandleIntent %s",mUserId);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);

        refreshFollowerList(mUserId, 0);

    }

    private void refreshFollowerList(String profileId, final int offset) {
        FollowRequestModel request = new FollowRequestModel();
        request.setProfile_id(profileId);
        request.setNextId(offset);
        request.setLimit(Constants.PAGE_LIMITED_1000);
        mCompositeSubscription.add(AppsterWebServices.get().getFollowersUsers(AppsterUtility.getAuth(), request)
                .observeOn(Schedulers.newThread())
                .map(BaseResponse::getData)
                .subscribe(followResponseModel -> {
                    Timber.d("refreshFollowerList response ok");
                    /*Realm realm = Realm.getDefaultInstance();
                    if (offset == 0) {
                        Timber.d("deleteAllFollowerInRealm");
                        deleteAllFollowerInRealm(realm);
                    }
                    realm.executeTransaction(r -> r.copyToRealmOrUpdate(followResponseModel.getResult()));

                    if (!followResponseModel.isEnd()) {
                        int offset1 = followResponseModel.getNextId();
                        refreshFollowerList(profileId, offset1);
                    } else {
                        saveLastTimeSync(System.currentTimeMillis());
                    }
                    realm.close();*/
                    // vinhtv
                    /*FollowingLocalDbRxHelper helper = new FollowingLocalDbRxHelper(AppsterApplication.mDatabase.followItemDao());
                    if(offset == 0) {
                        deleteAllFollower(helper);
                    }
                    helper.bulkInsert(followResponseModel.getResult()).subscribe();

                    if(!followResponseModel.isEnd()) {
                        int offset1 = followResponseModel.getNextId();
                        refreshFollowerList(profileId, offset1);
                    } else {
                        saveLastTimeSync(System.currentTimeMillis());
                    }*/
                }, this::handleError));

    }


    private void handleError(Throwable e) {
        Timber.e(e);
    }

    private void deleteAllFollower(FollowingLocalDbRxHelper helper) {
        // vinhtv
//        realm.executeTransaction(r -> r.delete(FollowItemModel.class));
        helper.erase().subscribe();

        saveLastTimeSync(Integer.MIN_VALUE);
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_time_delete", new Date(System.currentTimeMillis()).toString());
        editor.apply();
    }

    @Override
    public void onDestroy() {
//        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        super.onDestroy();
    }

    public static boolean shouldRefreshFollowerList() {
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        long lastTime = sharedPreferences.getLong(LAST_TIME_SYNC_FOLLOWER_LIST, Integer.MIN_VALUE);
        if(lastTime == Integer.MIN_VALUE) return true;
        long currentTime = System.currentTimeMillis();
        return currentTime - lastTime >= ONE_DAY;
    }

    private void saveLastTimeSync(long currentTime) {
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME_SYNC_FOLLOWER_LIST, currentTime);
        editor.apply();
    }

    public static void clearLastTimeSync(){
        SharedPreferences sharedPreferences = AppsterApplication.mAppPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME_SYNC_FOLLOWER_LIST, Integer.MIN_VALUE);
        editor.apply();
    }
}
