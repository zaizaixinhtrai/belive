package com.appster.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by sonnguyen on 9/10/15.
 */
public class AppsterInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMIntentServices.class);
        startService(intent);
    }
}
