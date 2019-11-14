package com.appster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.ads.conversiontracking.InstallReceiver;

import io.branch.referral.InstallListener;

/**
 * Created by linh on 18/04/2017.
 */

public class InstallReferralReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //branch io
        InstallListener branchIo = new InstallListener();
        branchIo.onReceive(context, intent);

        //google tracking
        InstallReceiver googleTracking = new InstallReceiver();
        googleTracking.onReceive(context, intent);

    }
}
