package com.appster.features.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * Created by thanhbc on 10/12/17.
 */

public class IncomingBroadcastReceiver extends BroadcastReceiver {
    public static boolean wasRinging;
    public static final String BELIVE_CALL_DETEECTOR = "belive_call_detector";
    @Override
    public void onReceive(Context context, Intent intent) {
        PhoneStateChangeListener pscl = new PhoneStateChangeListener(context);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(pscl, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //region phonecall
    private static class PhoneStateChangeListener extends PhoneStateListener {
        private Context mContext;
        private PhoneStateChangeListener(Context context) {
            mContext = new WeakReference<>(context).get();
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Timber.e("RINGING");
                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Timber.e("OFFHOOK");

                    if (wasRinging) {
                        // Start your new activity
                        if(mContext!=null) {
                            Intent pushNotification = new Intent(BELIVE_CALL_DETEECTOR);
                            pushNotification.putExtra("state", state);
                            LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(pushNotification);
                        }
                    } else {
                        // Cancel your old activity
                    }

                    // this should be the last piece of code before the break
//                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Timber.e("IDLE");
                    // this should be the last piece of code before the break
                    wasRinging = false;
                    break;
            }
        }
    }
    //endregion
}
