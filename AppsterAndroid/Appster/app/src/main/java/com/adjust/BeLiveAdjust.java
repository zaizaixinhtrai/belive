package com.adjust;


/*
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.appster.webservice.response.RegisterWithFacebookResponseModel;
*/
/**
 * Created by gaku on 4/26/17.
 */

/*
public class BeLiveAdjust {

    /*
    public static final String TAG = BeLiveAdjust.class.getSimpleName();

    public static void trackEvent(Event event) {
        Log.d(TAG, event.name());
        Adjust.trackEvent(new AdjustEvent(event.code));
    }

    public static void trackCreateAccountEvent(int registerType) {
        BeLiveAdjust.trackEvent(BeLiveAdjust.Event.CREATE_ACCOUNT);
        if (registerType == RegisterWithFacebookResponseModel.RegisterTypes.Facebook.type) {
            BeLiveAdjust.trackEvent(BeLiveAdjust.Event.CREATE_ACCOUNT_FB);
        } else if (registerType == RegisterWithFacebookResponseModel.RegisterTypes.Google.type) {
            BeLiveAdjust.trackEvent(BeLiveAdjust.Event.CREATE_ACCOUNT_GOOGLE);
        } else if (registerType == RegisterWithFacebookResponseModel.RegisterTypes.Instagram.type) {
            BeLiveAdjust.trackEvent(BeLiveAdjust.Event.CREATE_ACCOUNT_INSTAGRAM);
        } else if (registerType == RegisterWithFacebookResponseModel.RegisterTypes.Twitter.type) {
            BeLiveAdjust.trackEvent(BeLiveAdjust.Event.CREATE_ACCOUNT_TWITTER);
        }
    }

    public static void trackEnterStream(boolean isRecorded) {
        BeLiveAdjust.trackEvent(Event.ENTER_STREAM);

        if (isRecorded) {
            BeLiveAdjust.trackEvent(Event.ENTER_STREAM_RECORDED);
        } else {
            BeLiveAdjust.trackEvent(Event.ENTER_STREAM_LIVE);
        }
    }

    public static void trackSendGift(String giftId) {
        BeLiveAdjust.trackEvent(Event.SEND_GIFT);

        switch (giftId) {
            case "1":  BeLiveAdjust.trackEvent(Event.SEND_GIFT_01_LOVE); break;
            case "2":  BeLiveAdjust.trackEvent(Event.SEND_GIFT_03_TRUFFLES); break;
            case "4":  BeLiveAdjust.trackEvent(Event.SEND_GIFT_04_PERFUME); break;
            case "8":  BeLiveAdjust.trackEvent(Event.SEND_GIFT_11_SUPERCAR); break;
            case "11": BeLiveAdjust.trackEvent(Event.SEND_GIFT_02_WINE); break;
            case "12": BeLiveAdjust.trackEvent(Event.SEND_GIFT_05_ROSE); break;
            case "13": BeLiveAdjust.trackEvent(Event.SEND_GIFT_08_WATCH); break;
            case "14": BeLiveAdjust.trackEvent(Event.SEND_GIFT_12_MANSION); break;
            case "15": BeLiveAdjust.trackEvent(Event.SEND_GIFT_07_TEDDY); break;
            case "16": BeLiveAdjust.trackEvent(Event.SEND_GIFT_09_RING); break;
            case "17": BeLiveAdjust.trackEvent(Event.SEND_GIFT_10_LUXURY_CAR); break;
        }
    }

    public static void trackBuyGem(String itemId) {
        BeLiveAdjust.trackEvent(Event.PURCHASE_GEM);
        switch (itemId) {
            case "com.appstersgiprod.topup1": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_100); break;
            case "com.appstersgiprod.topup2": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_310); break;
            case "com.appstersgiprod.topup3": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_520); break;
            case "com.appstersgiprod.topup4": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_1650); break;
            case "com.appstersgiprod.topup5": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_5k); break;
            case "com.appstersgiprod.topup6": BeLiveAdjust.trackEvent(Event.PURCHASE_GEM_12k); break;
        }
    }

    public enum Event {
        CREATE_ACCOUNT("lwn5pf"),
        CREATE_ACCOUNT_FB("u7k2de"),
        CREATE_ACCOUNT_GOOGLE("a6ljoi"),
        CREATE_ACCOUNT_INSTAGRAM("6q20xs"),
        CREATE_ACCOUNT_TWITTER("oly4th"),

        SIGN_IN_LATER("fwhgd5"),

        ENTER_STREAM("aphg7c"),
        ENTER_STREAM_LIVE("m8fajn"),
        ENTER_STREAM_RECORDED("g94wb1"),

        LIVE_STREAM_SAVE("qawes3"),
        LIVE_STREAM_START("6zg0l3"),

        PURCHASE_GEM("pehrog"),
        PURCHASE_GEM_100("gwvcmx"),
        PURCHASE_GEM_310("7nwqry"),
        PURCHASE_GEM_520("zgoox8"),
        PURCHASE_GEM_1650("xa6gw2"),
        PURCHASE_GEM_5k("s42buj"),
        PURCHASE_GEM_12k("u9xrd4"),

        SEND_GIFT("bb7h5b"),
        SEND_GIFT_01_LOVE("svqmef"),
        SEND_GIFT_02_WINE("sob6li"),
        SEND_GIFT_03_TRUFFLES("e4l7km"),
        SEND_GIFT_04_PERFUME("ws2uw4"),
        SEND_GIFT_05_ROSE("gep9j1"),
        //SEND_GIFT_06_PERFUME("2bhaae"),
        SEND_GIFT_07_TEDDY("a5i78p"),
        SEND_GIFT_08_WATCH("d8mgeq"),
        SEND_GIFT_09_RING("6zriz4"),
        SEND_GIFT_10_LUXURY_CAR("6xegl7"),
        SEND_GIFT_11_SUPERCAR("6kajlg"),
        SEND_GIFT_12_MANSION("e6k418"),

        INVITE_FB("bdowvp"),
        INVITE_FB_SENT("t6m1uq"),
        INVITE_WP("qih6fm"),
        INVITE_TW("cvs191"),
        INVITE_EM("oie4pp"),
        INVITE_OTHER("x2xnrr"),

        ENTER_HOME("o9oa6g"),

        ;
        public final String code;
        Event(String code) {
            this.code = code;
        }
    }
}
*/
