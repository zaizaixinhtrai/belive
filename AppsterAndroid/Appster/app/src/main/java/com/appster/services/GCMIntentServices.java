package com.appster.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.UpdateDeviceTokenRequestModel;
import com.appster.data.AppPreferences;
import com.apster.common.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by sonnguyen on 9/10/15.
 */
public class GCMIntentServices extends IntentService {
    private static final String TAG = "GCMIntentServices";
    private static final int RETRY_TIMES = 5;

    public GCMIntentServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]

                Log.i(TAG, "GCM Registration Token: " + token);
                // TODO: Implement this method to send any registration to your app's servers.
                if (AppPreferences.getInstance(this).isUserLogin()) {
                    sendRegistrationToServer(token);
                }

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                Constants.device_token = token;
                AppPreferences.getInstance(this).saveDevicesToken(token);

            }
        } catch (Exception e) {
            Timber.e(e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.

        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.

    }



    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    @SuppressLint("RxLeakedSubscription")
    private void sendRegistrationToServer(String token) throws IOException {

        UpdateDeviceTokenRequestModel request = new UpdateDeviceTokenRequestModel();
        request.setDevice_token(token);
        request.setDevice_type(Constants.ANDROID_DEVICE_TYPE);
        request.setDevice_udid(AppsterApplication.mAppPreferences.getDevicesUDID());

        AppsterWebServices.get().updateDeviceToken("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .retryWhen(errors -> errors.zipWith(Observable.range(1, RETRY_TIMES), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((int) Math.pow(5, retryCount), TimeUnit.SECONDS)))
                .subscribe(updateDeviceTokenDataResponse -> {
                    Log.i(TAG, "GCM Registration Token success: " + token);
                }, error -> Timber.e("GCM: "+ error.getMessage()));
    }

}
