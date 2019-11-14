package com.appster.tracking;

import android.app.Application;
import android.content.Context;

import com.amplitude.api.Amplitude;
import com.amplitude.api.AmplitudeClient;
import com.amplitude.api.Revenue;
import com.appster.BuildConfig;
import com.appster.models.UserModel;
import com.appster.utility.CrashlyticsUtil;

import org.json.JSONObject;


/**
 * Created by An Nguyen on 7/18/16.
 */
public class EventTracker {
    private volatile static AmplitudeClient amplitude = null;
    private static String uid = "";
    private static String appID = "";

    private EventTracker() {

    }

    public static void initialize(final Context context, final Application application, final UserModel userModel) {
        appID = BuildConfig.AMPLITUDE;
        amplitude = Amplitude.getInstance();

        setOptOut(userModel);

        amplitude.initialize(context.getApplicationContext(), appID).enableForegroundTracking(application);
    }

    public static void setUser(UserModel userModel) {
        if (amplitude == null || userModel == null) {
            return;
        }

        amplitude.setUserId(userModel.getUserName());
    }

    public static void setUID(final String userID) {
        uid = userID;
    }

    public static void trackAppAccess() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_START_APP);
    }

    public static void trackRegisterSuccess() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_REGISTER_SUCCESS);
    }

    public static void trackLoginSuccess() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_LOGIN_SUCCESS);
    }

    public static void trackExitAppWithoutRegister() {
        if (amplitude == null) {
            return;
        }

        if (uid.isEmpty()) {
            amplitude.logEvent(EventTrackingName.EVENT_EXIT_WITHOUT_REGISTER);
        }
    }


    public static void trackSharePost(final String social) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put("social_network", social);
            amplitude.logEvent(EventTrackingName.EVENT_VIEWER_SHARE_POST, event);
        } catch (Exception ex) {
            CrashlyticsUtil.logException(ex);
        }
    }


    public static void trackShareFacebook() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_FACEBOOK);
    }

    public static void trackViewerShareStream(final String socialNetwork) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put("social_network", socialNetwork);
            amplitude.logEvent(EventTrackingName.EVENT_VIEWER_SHARE_STREAM, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackShareTwitter() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_TWITTER);
    }

    public static void trackShareInstagram() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_INSTAGRAM);
    }

    public static void trackShareWhatsApp() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_WHATSAPP);
    }

    public static void trackShareEmail() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_EMAIL);
    }

    public static void trackShareWeChat() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_WECHAT);
    }

    public static void trackShareWeibo() {
        if (amplitude == null) {
            return;
        }

        amplitude.logEvent(EventTrackingName.EVENT_SHARE_WEIBO);
    }

    public static void trackSelectCategory(final String catelogy) {
        if (amplitude == null) {
            return;
        }

        try {
            if (catelogy != null) {
                JSONObject event = new JSONObject();
                event.put(catelogy, catelogy);
                amplitude.logEvent(EventTrackingName.EVENT_SELECT_CATEGORY, event);
            }
        } catch (Exception ee) {
            CrashlyticsUtil.logException(ee);
        }
    }

    public static void trackEvent(final String eventName) {
        if (amplitude == null) {
            return;
        }
        try {
            amplitude.logEvent(eventName);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackEvent(final String eventName, String propName, Object propValue) {
        if (amplitude == null) {
            return;
        }
        try {
            JSONObject prop = new JSONObject();
            prop.put(propName, propValue);
            amplitude.logEvent(eventName, prop);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackEnterStream(boolean isRecorded, int streamId) {
        trackEvent(EventTrackingName.EVENT_ENTER_STREAM, EventTrackingName.STREAM_ID, streamId);

        if (isRecorded) {
            trackEvent(EventTrackingName.EVENT_ENTER_STREAM_RECORDED, EventTrackingName.STREAM_ID, streamId);
        } else {
            trackEvent(EventTrackingName.EVENT_ENTER_STREAM_LIVE, EventTrackingName.STREAM_ID, streamId);
        }
    }

    public static void trackRevenue(String productId, String price) {

        double priceValue = 0;
        try {
            priceValue = Double.valueOf(price);
        } catch (Exception e) {
        }

        Revenue revenue = new Revenue();
        revenue.setProductId(productId);
        revenue.setPrice(priceValue);
        revenue.setQuantity(1);
        revenue.setProductId(productId);
        amplitude.logRevenueV2(revenue);
    }

    public static void trackEventBanner(String bannerId) {
        trackEvent(EventTrackingName.EVENT_BANNER, EventTrackingName.BANNER_ID, bannerId);
    }

    public static void trackPushNotification(final String pushMessage, final String pushType) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.PUSH_MESSAGE, pushMessage);
            event.put(EventTrackingName.PUSH_TYPE, pushType);
            amplitude.logEvent(EventTrackingName.EVENT_PUSH_NOTIFICATION, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackPushNotification(final String pushMessage, final String pushType, final String slugID) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.PUSH_MESSAGE, pushMessage);
            event.put(EventTrackingName.PUSH_TYPE, pushType);
            event.put(EventTrackingName.SLUG_ID, slugID);
            amplitude.logEvent(EventTrackingName.EVENT_PUSH_NOTIFICATION, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void setOptOut(UserModel userModel) {
        if (userModel != null && userModel.isDevUser()) {
            amplitude.trackSessionEvents(false);
            amplitude.setOptOut(true);
        } else {
            amplitude.trackSessionEvents(true);
            amplitude.setOptOut(false);
        }
    }

    public static void trackingReferralCode(final int requesterUserId, final int receiverUserId,
                                            final boolean isTriviaRequester, final boolean isTriviaReceiver) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.REQUESTER_USER_ID, requesterUserId);
            event.put(EventTrackingName.RECEIVER_USER_ID, receiverUserId);
            event.put(EventTrackingName.IS_TRIVIA_REQUESTER, isTriviaRequester);
            event.put(EventTrackingName.IS_TRIVIA_RECEIVER, isTriviaReceiver);
            amplitude.logEvent(EventTrackingName.EVENT_REFERRAL, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackPointsTab(final String userId) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.USER_ID, userId);
            amplitude.logEvent(EventTrackingName.EVENT_POINTS_TAB, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackPointsOpenBox(final String userId, final String type) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.USER_ID, userId);
            amplitude.logEvent(type, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackLeftMenuPointsTab(final String userId) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.USER_ID, userId);
            amplitude.logEvent(EventTrackingName.EVENT_POINT_TAP_POINT_LEFT_MENU, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }

    public static void trackMBPointsTab(final String userId) {
        if (amplitude == null) {
            return;
        }

        try {
            JSONObject event = new JSONObject();
            event.put(EventTrackingName.USER_ID, userId);
            amplitude.logEvent(EventTrackingName.EVENT_POINT_TAP_POINT_MB, event);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
        }
    }
}
