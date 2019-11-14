package com.appster.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by thanhbc on 5/22/18.
 */

public class LiveShowReceiver extends BroadcastReceiver {
    public static final String SHOWID = "liveshow_id";
    public static final String LIVE_SHOW_ACTION = "belive-live-show";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(LIVE_SHOW_ACTION);
        i.putExtra(SHOWID, intent.getIntExtra(SHOWID,0));
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }
}
