package com.appster.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appster.utility.AppsterUtility;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

import timber.log.Timber;

/**
 * Created by thanhbc on 1/8/17.
 */

public class InternalMessageReceiver extends BroadcastReceiver {
    public static final String STREAM_ID_KEY = "stream_id";
    public static final String STREAM_USER_ID_KEY = "stream_user_id";

    @Override
    public void onReceive(Context context, Intent intent) {

//        NotificationModel.NotificationEntity notifiactionEntity;
        String slug = intent.getStringExtra(STREAM_ID_KEY);
        String userId = intent.getStringExtra(STREAM_USER_ID_KEY);
//        if (intent != null) {
//            notifiactionEntity = intent.getExtras().getParcelable(ConstantBundleKey.BUNDLE_NOTIFICATION_KEY);
//            slug = ;
//            userId = ;
//            if (notifiactionEntity != null) {
//                EventBus.getDefault().post(notifiactionEntity);
//            } else
        if (!StringUtil.isNullOrEmptyString(slug) && !StringUtil.isNullOrEmptyString(userId)) {
            Timber.e("receive key -> %s user %s", slug, userId);
            AppsterUtility.removePrefListItem(context, Constants.STREAM_MUTE_LIST, userId, slug);
        }
//        }
    }
}
